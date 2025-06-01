package com.tablelog.tablelogback.domain.recipe.service.impl;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.ForbiddenAccessRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.mapper.entity.RecipeEntityMapper;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepositoryImpl;
import com.tablelog.tablelogback.domain.recipe.service.RecipeService;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import com.tablelog.tablelogback.domain.recipe_food.mapper.entity.RecipeFoodEntityMapper;
import com.tablelog.tablelogback.domain.recipe_food.repository.RecipeFoodRepository;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_payment.repository.RecipePaymentRepository;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessDto;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import com.tablelog.tablelogback.domain.recipe_process.mapper.entity.RecipeProcessEntityMapper;
import com.tablelog.tablelogback.domain.recipe_process.repository.RecipeProcessRepository;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.s3.S3Provider;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeEntityMapper recipeEntityMapper;
    private final FoodRepository foodRepository;
    private final RecipeFoodRepository recipeFoodRepository;
    private final RecipeProcessRepository recipeProcessRepository;
    private final RecipeFoodEntityMapper recipeFoodEntityMapper;
    private final RecipeProcessEntityMapper recipeProcessEntityMapper;
    private final S3Provider s3Provider;
    private final RecipeRepositoryImpl recipeRepositoryImpl;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeSaveRepository recipeSaveRepository;
    private final UserRepository userRepository;
    private final RecipePaymentRepository recipePaymentRepository;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";

    @Override
    public void createRecipe(
            final RecipeCreateServiceRequestDto requestDto,
            final MultipartFile recipeImage,
            final List<RecipeFoodCreateServiceRequestDto> rfRequestDtos,
            final RecipeProcessCreateRequestDto rpRequestDtos,
            final User user
    ) throws IOException {
        String recipeFolderName = requestDto.title() + UUID.randomUUID();
        String recipeImageName = null;
        if (recipeImage != null) {
            recipeImageName = recipeFolderName + SEPARATOR + s3Provider.originalFileName(recipeImage);
        }
        Recipe recipe = recipeEntityMapper.toRecipe(
                requestDto, recipeFolderName, s3Provider.getImagePath(recipeImageName), user);

        // 전문가 & 유료 확인
        if (user.getUserRole() != UserRole.EXPERT || !requestDto.isPaid()) {
            recipe.updateIsPaid(false);
            recipe.updateRecipePoint(0);
        }
        recipe.updateTotalCal(0);
        recipeRepository.save(recipe);
        user.updateRecipeCount(user.getRecipeCount() + 1);

        // 레시피 식재료 생성
        Integer totalCal = 0;
        List<RecipeFood> recipeFoods = new ArrayList<>();
        for (int i = 0; i < rfRequestDtos.size(); i++) {
            RecipeFoodCreateServiceRequestDto rfDto = rfRequestDtos.get(i);
            Food food = foodRepository.findById(rfDto.foodId())
                    .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));

            // 칼로리 계산
            double userAmountInBase = rfDto.recipeFoodUnit().toBaseUnit(rfDto.amount());
            double foodUnitToBase = food.getFoodUnit().toBaseUnit(1.0);
            double caloriePerBaseUnit = food.getCal() / foodUnitToBase;
            int cal = (int) (userAmountInBase * caloriePerBaseUnit);
            totalCal += cal;

            RecipeFood recipeFood = recipeFoodEntityMapper.toRecipeFood(rfRequestDtos.get(i), recipe.getId(), food);
            recipeFoods.add(recipeFood);
        }
        recipe.updateTotalCal(totalCal);
        recipeFoodRepository.saveAll(recipeFoods);

        // 레시피 조리과정 생성
        List<RecipeProcess> recipeProcesses = new ArrayList<>();
        List<String> rpImageNames = new ArrayList<>();
        List<MultipartFile> recipeProcessImages = new ArrayList<>();

        for (RecipeProcessDto rpDto : rpRequestDtos.dtos()) {
            List<String> imageUrls = new ArrayList<>();

            List<MultipartFile> files = rpDto.files();
            // 사이즈 3개로 제한
            int s = 0;
            if (files != null && s <= 2) {
                for (MultipartFile image : files) {
                    if (image != null && !image.isEmpty()) {
                        String fileName = s3Provider.originalFileName(image);
                        String filePath = recipeFolderName + S3Provider.SEPARATOR + fileName;
                        String fileUrl = url + filePath;

                        imageUrls.add(fileUrl);
                        rpImageNames.add(fileName);
                        recipeProcessImages.add(image);

                        s++;
                    }
                }
            }

            RecipeProcess process = recipeProcessEntityMapper.toRecipeProcess(recipe.getId(), rpDto, imageUrls);
            recipeProcesses.add(process);
        }
        recipeProcessRepository.saveAll(recipeProcesses);

        saveImage(recipe.getFolderName(), recipeImage, recipeImageName, recipeProcessImages, rpImageNames);

        if(user.getRecipeCount() >= 50 && user.getUserRole() == UserRole.NORMAL){
            user.changeRole(UserRole.EXPERT);
        }
        user.addPointBalance(3000);
        userRepository.save(user);
    }

    private void saveImage(
            String recipeFolderName,
            MultipartFile recipeImage,
            String recipeImageName,
            List<MultipartFile> rpImage,
            List<String> rpImageName
    ) throws IOException {
        s3Provider.createFolder(recipeFolderName);
        if(recipeImage != null && !recipeImage.isEmpty()) {
            s3Provider.saveFile(recipeImage, recipeImageName);
        }
        if(rpImage != null) {
            for (int i = 0; i < rpImage.size(); i++) {
                MultipartFile image = rpImage.get(i);
                if (!image.isEmpty()) {
                    s3Provider.saveFile(image, recipeFolderName + S3Provider.SEPARATOR + rpImageName.get(i));
                }
            }
        }
    }

    @Override
    public RecipeReadResponseDto readRecipe(Long id, UserDetailsImpl userDetails){
        Recipe recipe = findRecipe(id);
        Long likeCount = recipeLikeRepository.countByRecipe(id);
        Boolean isSaved = isSaved(userDetails, id);
        User user = userRepository.findById(recipe.getUserId())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Boolean isWriter = user.getId().equals(recipe.getUserId());
        Boolean hasPurchased = userDetails != null
                && recipePaymentRepository.existsByUserIdAndRecipeId(user.getId(), recipe.getId());
        return recipeEntityMapper.toRecipeReadDetailResponseDto(recipe, likeCount,
                isSaved, user.getNickname(), isWriter, hasPurchased);
    }

    @Override
    public RecipeFoodPreviewDto readRecipeWithRecipeFood(Long id){
        Recipe recipe = findRecipe(id);
        List<RecipeFood> recipeFoods = recipeFoodRepository
                .findAllByRecipeId(id, PageRequest.of(0, 5))
                .getContent();
        return recipeEntityMapper.toRecipeFoodPreviewReadResponseDto(recipe, recipeFoods);
    }

    @Override
    public RecipeSliceResponseDto readAllRecipes(int pageNumber, UserDetailsImpl user, Boolean isPaid){
        PageRequest pageRequest = PageRequest.of(pageNumber, 10, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice;
        if(isPaid == null || !isPaid) {
            slice = recipeRepository.findAll(pageRequest);
        } else {
            slice = recipeRepository.findAllByIsPaidTrue(pageRequest);
        }
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
//        List<RecipeReadAllServiceResponseDto> recipes =
//                recipeEntityMapper.toRecipeReadAllResponseDto(slice.getContent());
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readPopularRecipes(int pageNumber, UserDetailsImpl user) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findPopularRecipesLastWeek(oneWeekAgo, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipeByUser(Long id, int pageNumber, UserDetailsImpl user) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findAllByUserId(id, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto getAllMyRecipes(UserDetailsImpl userDetails, int pageNumber, Boolean isPaid) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice;
        if(isPaid == null || !isPaid) {
            slice = recipeRepository.findAllByUserId(userDetails.user().getId(), pageRequest);
        } else {
            slice = recipeRepository.findAllByIsPaidTrueAndUserId(userDetails.user().getId(), pageRequest);
        }
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, userDetails);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipeByFoodName(String keyword, int pageNumber, UserDetailsImpl user){
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.searchRecipesByFoodName(keyword, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto filterRecipes(RecipeFilterConditionDto condition, int pageNumber, UserDetailsImpl user){
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepositoryImpl.findAllByFilter(condition, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Transactional
    public void updateRecipe(
            Long id, RecipeUpdateServiceRequestDto requestDto,
            User user, MultipartFile multipartFile
    ) throws  IOException{
        Recipe recipe = validateRecipe(id, user);

        String folderName = recipe.getFolderName();
        String fileUrl = s3Provider.updateImage(recipe.getImageUrl(), folderName, multipartFile);

        recipe.updateRecipe(requestDto.title(), requestDto.intro(), folderName, fileUrl,
                requestDto.recipeCategoryList(), requestDto.price(), requestDto.memo(), requestDto.cookingTime(),
                requestDto.isPaid(), requestDto.recipePoint()
        );

        // 전문가 & 유료 확인
        if (user.getUserRole() != UserRole.EXPERT || !requestDto.isPaid()) {
            recipe.updateIsPaid(false);
            recipe.updateRecipePoint(0);
        }

        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(Long id, User user) {
        Recipe recipe = validateRecipe(id, user);
        // 유료 레시피면 삭제 불가
        if(recipe.getIsPaid()){
            throw new ForbiddenAccessRecipeException(RecipeErrorCode.FORBIDDEN_ACCESS_RECIPE);
        }
        recipeFoodRepository.deleteAllByRecipeId(id);
        recipeProcessRepository.deleteAllByRecipeId(id);
        recipeRepository.delete(recipe);
        s3Provider.delete(recipe.getFolderName());
        user.updateRecipeCount(user.getRecipeCount() - 1);
    }

    private Recipe validateRecipe(Long recipeId, User user){
        Recipe recipe = findRecipe(recipeId);
        if (!Objects.equals(recipe.getUserId(), user.getId()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeException(RecipeErrorCode.FORBIDDEN_ACCESS_RECIPE);
        }
        return recipe;
    }

    private Recipe findRecipe(Long id){
        return recipeRepository.findById(id)
                .orElseThrow(()-> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
    }

    private List<RecipeReadAllServiceResponseDto> mappingRecipes(Slice<Recipe> slice, UserDetailsImpl userDetails){
        List<Long> recipeIds = slice.getContent().stream()
                .map(Recipe::getId)
                .collect(Collectors.toList());

        // 작성자 id
        List<Long> userIds = slice.getContent().stream()
                .map(Recipe::getUserId)
                .distinct()
                .toList();

        // 작성자 이름 조회
        Map<Long, String> userIdToNickname = userRepository.findNicknamesByUserIds(userIds).stream()
                .collect(Collectors.toMap(RecipeUserNicknameDto::userId, RecipeUserNicknameDto::nickname));

        // 좋아요 개수
        Map<Long, Long> likeCountMap = recipeLikeRepository.countLikesByRecipeIds(recipeIds).stream()
                .collect(Collectors.toMap(RecipeLikeCountDto::recipeId, RecipeLikeCountDto::likeCount));

        // 저장 여부
        final Map<Long, Boolean> isSavedMap = (userDetails != null)
                ? recipeSaveRepository.findSavesByRecipeAndUser(recipeIds, userDetails.user().getId())
                .stream()
                .collect(Collectors.toMap(
                        RecipeIsSavedDto::recipeId,
                        RecipeIsSavedDto::isSaved
                ))
                : Collections.emptyMap();

        Long userId = (userDetails != null) ? userDetails.user().getId() : null;

        List<RecipeReadAllServiceResponseDto> recipes = slice.getContent().stream()
                .map(recipe -> {
                    Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                    Boolean isSaved = isSavedMap.getOrDefault(recipe.getId(), false);
                    String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                    Boolean isWriter = userId != null && userId.equals(recipe.getUserId());
                    return recipeEntityMapper.toRecipeReadResponseDto(recipe, likeCount, isSaved, nickname, isWriter);
                })
                .collect(Collectors.toList());
        return recipes;
    }

    private Boolean isSaved(UserDetailsImpl userDetails, Long id){
        Boolean isSaved = false;
        if (userDetails != null) {
            Long userId = userDetails.user().getId();
            isSaved = recipeSaveRepository.existsByRecipeAndUser(id, userId);
        }
        return isSaved;
    }
}
