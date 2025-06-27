package com.tablelog.tablelogback.domain.recipe_process.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.*;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import com.tablelog.tablelogback.domain.recipe_process.exception.ForbiddenAccessRecipeProcessException;
import com.tablelog.tablelogback.domain.recipe_process.exception.NotFoundRecipeProcessException;
import com.tablelog.tablelogback.domain.recipe_process.exception.RecipeProcessErrorCode;
import com.tablelog.tablelogback.domain.recipe_process.mapper.entity.RecipeProcessEntityMapper;
import com.tablelog.tablelogback.domain.recipe_process.repository.RecipeProcessRepository;
import com.tablelog.tablelogback.domain.recipe_process.service.RecipeProcessService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.s3.S3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class RecipeProcessServiceImpl implements RecipeProcessService {

    private final RecipeProcessRepository recipeProcessRepository;
    private final RecipeProcessEntityMapper recipeProcessEntityMapper;
    private final RecipeRepository recipeRepository;
    private final S3Provider s3Provider;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";

    @Override
    public void createRecipeProcess(
            final Long recipeId, final RecipeProcessCreateServiceRequestDto serviceRequestDto,
            final List<MultipartFile> recipeProcessImages, User user
    ) throws IOException {
        validateRecipeProcess(recipeId, user);

        List<String> recipeProcessImageUrls = new ArrayList<>();
        String folderName = user.getFolderName();

        if (recipeProcessImages != null && !recipeProcessImages.isEmpty()) {
            for (int i = 0; i < recipeProcessImages.size(); i++) {
                MultipartFile image = recipeProcessImages.get(i);
                if (!image.isEmpty()) {
                    String fileName = s3Provider.originalFileName(image);
                    String filePath = folderName + S3Provider.SEPARATOR + fileName;
                    String fileUrl = url + filePath;

                    s3Provider.saveFile(image, filePath);
                    recipeProcessImageUrls.add(fileUrl);
                }
            }
        }

        RecipeProcess recipeProcess = recipeProcessEntityMapper.toRecipeProcess(
                recipeId, serviceRequestDto, recipeProcessImageUrls);
        recipeProcessRepository.save(recipeProcess);
    }

    @Override
    public RecipeProcessReadAllServiceResponseDto readRecipeProcess(Long recipeId, Long recipeProcessId){
        RecipeProcess recipeProcess = findRecipeProcess(recipeProcessId);
        return recipeProcessEntityMapper.toRecipeProcessReadResponseDto(recipeProcess);
    }

    @Override
    public RecipeProcessSliceResponseDto readRecipeProcessWithSequence(Long recipeId, Long sequence){
        RecipeProcess recipeProcess = recipeProcessRepository.findByRecipeIdAndSequence(recipeId, sequence)
                .orElseThrow(() -> new NotFoundRecipeProcessException(RecipeProcessErrorCode.NOT_FOUND_RECIPE_PROCESS));
        PageRequest pageRequest = PageRequest.of(Math.toIntExact(sequence), 1);
        Slice<RecipeProcess> slice = recipeProcessRepository.findAllByRecipeId(recipeId, pageRequest);
        RecipeProcessReadAllServiceResponseDto raDto =
                recipeProcessEntityMapper.toRecipeProcessReadResponseDto(recipeProcess);
        return new RecipeProcessSliceResponseDto(raDto, slice.hasPrevious(), slice.hasNext());
    }

    @Override
    public RecipeProcessReadAllSliceResponseDto readAllRecipeProcessesByRecipeId(Long recipeId, int page) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        PageRequest pageRequest = PageRequest.of(page, 5);
        int totalCount = recipeProcessRepository.countByRecipeId(recipeId);
        Slice<RecipeProcess> slice = recipeProcessRepository.findAllByRecipeId(recipe.getId(), pageRequest);
        List<RecipeProcessReadAllServiceResponseDto> rpDtos =
                recipeProcessEntityMapper.toRecipeProcessReadAllResponseDto(slice.getContent());
        List<RecipeProcessSliceResponseDto> recipeProcesses =
                IntStream.range(0, rpDtos.size())
                        .mapToObj(i -> {
                            int globalIndex = page * 5 + i;
                            boolean hasPrev = globalIndex > 0;
                            boolean hasNext = globalIndex < totalCount - 1;
                            return new RecipeProcessSliceResponseDto(rpDtos.get(i), hasPrev, hasNext);
                        })
                        .toList();
        return new RecipeProcessReadAllSliceResponseDto(recipeProcesses, slice.hasNext(), totalCount);
    }

    @Transactional
    public void updateRecipeProcess(
            Long recipeId, Long recipeProcessId, RecipeProcessUpdateServiceRequestDto requestDto,
            List<MultipartFile> recipeProcessImages, User user
    ) throws IOException {
        validateRecipeProcess(recipeId, user);
        RecipeProcess recipeProcess = findRecipeProcess(recipeProcessId);

        List<String> recipeProcessImageUrls = new ArrayList<>();
        String folderName = user.getFolderName();

        if (recipeProcessImages != null && !recipeProcessImages.isEmpty()) {
            for (int i = 0; i < recipeProcessImages.size(); i++) {
                MultipartFile image = recipeProcessImages.get(i);
                if (!image.isEmpty()) {
                    String fileName = s3Provider.originalFileName(image);
                    String filePath = folderName + S3Provider.SEPARATOR + fileName;
                    String fileUrl = url + filePath;

                    s3Provider.saveFile(image, filePath);
                    recipeProcessImageUrls.add(fileUrl);
                }
            }
        }

        recipeProcess.updateRecipeProcess(requestDto.sequence(), requestDto.rpTitle(),
                requestDto.description(), recipeProcessImageUrls);
        recipeProcessRepository.save(recipeProcess);
    }

    @Override
    public void deleteRecipeProcess(Long recipeId, Long recipeProcessId, User user) {
        validateRecipeProcess(recipeId, user);
        RecipeProcess recipeProcess = findRecipeProcess(recipeProcessId);

        if (recipeProcess.getRecipeProcessImageUrls() != null){
            for(int i = 0; i < recipeProcess.getRecipeProcessImageUrls().size(); i++) {
                String image_name = recipeProcess.getRecipeProcessImageUrls().get(i).replace(url, "");
                image_name = image_name.substring(image_name.lastIndexOf("/"));
                s3Provider.delete(user.getFolderName() + image_name);
            }
        }
        recipeProcessRepository.delete(recipeProcess);
    }

    private void validateRecipeProcess(Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        if (!Objects.equals(recipe.getUserId(), user.getId()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeProcessException(RecipeProcessErrorCode.FORBIDDEN_ACCESS_RECIPE_PROCESS);
        }
    }

    private RecipeProcess findRecipeProcess(Long id){
        RecipeProcess recipeProcess = recipeProcessRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeProcessException(RecipeProcessErrorCode.NOT_FOUND_RECIPE_PROCESS));
        return recipeProcess;
    }
}
