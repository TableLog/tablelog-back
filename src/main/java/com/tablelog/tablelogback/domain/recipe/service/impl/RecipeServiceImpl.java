package com.tablelog.tablelogback.domain.recipe.service.impl;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.domain.point_transaction.repository.PointTransactionRepository;
import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.ForbiddenAccessRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.mapper.entity.RecipeEntityMapper;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe.service.RecipeService;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import com.tablelog.tablelogback.domain.recipe_food.mapper.entity.RecipeFoodEntityMapper;
import com.tablelog.tablelogback.domain.recipe_food.repository.RecipeFoodRepository;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_memo.repository.RecipeMemoRepository;
import com.tablelog.tablelogback.domain.recipe_payment.repository.RecipePaymentRepository;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessDto;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import com.tablelog.tablelogback.domain.recipe_process.mapper.entity.RecipeProcessEntityMapper;
import com.tablelog.tablelogback.domain.recipe_process.repository.RecipeProcessRepository;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
import com.tablelog.tablelogback.domain.shopping_list.entity.ShoppingList;
import com.tablelog.tablelogback.domain.shopping_list.repository.ShoppingListRepository;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import com.tablelog.tablelogback.global.enums.PointReason;
import com.tablelog.tablelogback.global.enums.PointType;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.s3.AsyncImageUploadService;
import com.tablelog.tablelogback.global.s3.S3Provider;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
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
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeSaveRepository recipeSaveRepository;
    private final UserRepository userRepository;
    private final RecipePaymentRepository recipePaymentRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final RecipeMemoRepository recipeMemoRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final AsyncImageUploadService asyncImageUploadService;
    // 이미지 URL은 S3Provider의 getImagePath를 사용 (로컬 경로 기반)
    private final String SEPARATOR = "/";

    @Override
    @Transactional
    public void createRecipe(
            final RecipeCreateServiceRequestDto requestDto,
            final MultipartFile recipeImage,
            final List<RecipeFoodCreateServiceRequestDto> rfRequestDtos,
            final RecipeProcessCreateRequestDto rpRequestDtos,
            final User user
    ) throws IOException {
        String recipeFolderName = requestDto.title() + UUID.randomUUID();

        // ── 메인 이미지: URL 선행 계산 (업로드는 마지막에 비동기로)
        String recipeImageKey = null;
        byte[] recipeImageBytes = null;
        String recipeImageContentType = null;
        Recipe recipe;
        if (recipeImage != null && !recipeImage.isEmpty()) {
            recipeImageKey = recipeFolderName + SEPARATOR + s3Provider.originalFileName(recipeImage);
            recipeImageBytes = recipeImage.getBytes();
            recipeImageContentType = recipeImage.getContentType();
            recipe = recipeEntityMapper.toRecipe(
                    requestDto, recipeFolderName, s3Provider.getImagePath(recipeImageKey), user, 500);
        } else {
            recipe = recipeEntityMapper.toRecipe(
                    requestDto, recipeFolderName, null, user, 500);
        }

        // 전문가 & 유료 확인
        if (user.getUserRole() != UserRole.EXPERT || !requestDto.isPaid()) {
            recipe.updateIsPaid(false);
            recipe.updateRecipePoint(0);
        }
//        recipe.updateTotalCal(0D);
        recipeRepository.save(recipe);
        user.updateRecipeCount(user.getRecipeCount() + 1);

        // ── 식재료 N+1 해결: 한 번에 일괄 조회
        List<Long> foodIds = rfRequestDtos.stream()
                .map(RecipeFoodCreateServiceRequestDto::foodId)
                .distinct()
                .toList();
        Map<Long, Food> foodMap = foodRepository.findAllById(foodIds).stream()
                .collect(Collectors.toMap(Food::getId, f -> f));

        Integer totalCal = 0;
        List<RecipeFood> recipeFoods = new ArrayList<>();
        for (RecipeFoodCreateServiceRequestDto rfDto : rfRequestDtos) {
            Food food = Optional.ofNullable(foodMap.get(rfDto.foodId()))
                    .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
//            totalCal += calculateCal(rfDto, food);
            recipeFoods.add(recipeFoodEntityMapper.toRecipeFood(rfDto, recipe, food.getId()));
        }
//        recipe.updateTotalCal(totalCal);
        recipeFoodRepository.saveAll(recipeFoods);

        // ── 조리과정: URL 선행 계산 후 bytes 수집
        List<RecipeProcess> recipeProcesses = new ArrayList<>();
        List<byte[]> rpImageBytesList = new ArrayList<>();
        List<String> rpImageContentTypes = new ArrayList<>();
        List<String> rpImageKeys = new ArrayList<>();

        for (RecipeProcessDto rpDto : rpRequestDtos.dtos()) {
            List<String> imageUrls = new ArrayList<>();
            List<MultipartFile> files = rpDto.files();
            int s = 0;
            if (files != null) {
                for (MultipartFile image : files) {
                    if (image != null && !image.isEmpty() && s < 3) {
                        String key = recipeFolderName + S3Provider.SEPARATOR + s3Provider.originalFileName(image);
                        imageUrls.add(s3Provider.getImagePath(key));
                        rpImageKeys.add(key);
                        rpImageBytesList.add(image.getBytes());
                        rpImageContentTypes.add(image.getContentType());
                        s++;
                    }
                }
            }
            recipeProcesses.add(recipeProcessEntityMapper.toRecipeProcess(recipe, rpDto, imageUrls));
        }
        recipeProcessRepository.saveAll(recipeProcesses);

        // ── 포인트 지급
        user.addPointBalance(3000);
        userRepository.save(user);
        PointTransaction pointTransaction = PointTransaction.builder()
                .userId(user.getId())
                .amount(3000)
                .pointReason(PointReason.레시피등록)
                .pointType(PointType.EARN)
                .build();
        pointTransactionRepository.save(pointTransaction);

        // ── S3 업로드 완료 후 응답 (이미지 깨짐 방지)
        try {
            CompletableFuture<Void> uploadFuture = asyncImageUploadService.uploadRecipeImages(
                    recipeFolderName,
                    recipeImageBytes, recipeImageContentType, recipeImageKey,
                    rpImageBytesList, rpImageContentTypes, rpImageKeys
            );
            uploadFuture.get(); // 업로드 완료될 때까지 대기
        } catch (Exception e) {
            log.error("[RecipeService] S3 업로드 실패. error: {}", e.getMessage(), e);
        }
    }

    @Override
    public RecipeReadResponseDto readRecipe(Long id, UserDetailsImpl userDetails){
        Recipe recipe = findRecipe(id);
        Long likeCount = recipeLikeRepository.countByRecipe(id);
        Boolean isSaved = isSaved(userDetails, id);
        User writer = userRepository.findById(recipe.getUserId()).orElse(null);
        String writerName = (writer != null) ? writer.getNickname() : "Unknown";
        Boolean isExpertWriter = writer != null && writer.getUserRole() == UserRole.EXPERT;
        Boolean isWriter = userDetails != null && userDetails.user().getId().equals(recipe.getUserId());
        Boolean hasPurchased = userDetails != null
                && recipePaymentRepository.existsByUserIdAndRecipeId(userDetails.user().getId(), recipe.getId());
        return recipeEntityMapper.toRecipeReadDetailResponseDto(recipe, likeCount,
                isSaved, writerName, isExpertWriter, isWriter, hasPurchased);
    }

    @Override
    public RecipeFoodPreviewSliceResponseDto readRecipeWithRecipeFood(Long id, int pageNum, UserDetailsImpl userDetails){
        Recipe recipe = findRecipe(id);
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<RecipeFood> slice = recipeFoodRepository.findAllByRecipeId(id, pageRequest);

        List<Long> foodIds = slice.stream()
                .map(RecipeFood::getFoodId)
                .distinct()
                .toList();

        Map<Long, Food> foodMap = foodRepository.findAllById(foodIds).stream()
                .collect(Collectors.toMap(Food::getId, food -> food));

        Map<Long, Long> existingFoodMap;
        if(userDetails != null){
            existingFoodMap = shoppingListRepository
                    .findAllByUserIdAndFoodIdIn(userDetails.user().getId(), foodIds)
                    .stream()
                    .collect(Collectors.toMap(ShoppingList::getFoodId, ShoppingList::getId));
        } else {
            existingFoodMap = Collections.emptyMap();
        }

        List<RecipeFoodPreviewDto> previewDtos = slice.stream()
                .map(rf -> {
                    Food food = foodMap.get(rf.getFoodId());
                    String foodName = food.getFoodName();
//                    int calorie = rf.getAmount() * food.getCal();
                    Long foodId = rf.getFoodId();
                    boolean isChecked = existingFoodMap.containsKey(foodId);
                    Long shoppingListId = isChecked ? existingFoodMap.get(foodId) : 0L;

                    return new RecipeFoodPreviewDto(
                            rf.getId(),
                            rf.getAmount(),
                            rf.getRecipeFoodUnit(),
                            rf.getFoodId(),
                            foodName,
//                            calorie,
                            isChecked,
                            shoppingListId
                    );
                })
                .toList();
        return new RecipeFoodPreviewSliceResponseDto(recipe.getTitle(), recipe.getImageUrl(), previewDtos, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipes(int pageNum, UserDetailsImpl user, Boolean isPaid){
        PageRequest pageRequest = PageRequest.of(pageNum, 10, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice;
        if(isPaid == null || !isPaid) {
            slice = recipeRepository.findAll(pageRequest);
        } else {
            slice = recipeRepository.findAllByIsPaidTrue(pageRequest);
        }
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readPopularRecipesLastWeek(int pageNum, UserDetailsImpl user) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findPopularRecipesLastWeek(oneWeekAgo, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        if(recipes.size() == 0){
            slice = recipeRepository.findPopularRecipes(pageRequest);
            recipes = mappingRecipes(slice, user);
        }
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readPopularRecipes(int pageNum, UserDetailsImpl user, Boolean isPaid) {
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice;
        if(isPaid == null || !isPaid) {
            slice = recipeRepository.findPopularRecipes(pageRequest);
        } else {
            slice = recipeRepository.findPopularRecipesByIsPaidTrue(pageRequest);
        }
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipeByUser(Long id, int pageNum, UserDetailsImpl user) {
        PageRequest pageRequest = PageRequest.of(pageNum, 9, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findAllByUserId(id, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllMyRecipesLatest(UserDetailsImpl userDetails, int pageNum, Boolean isPaid) {
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
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
    public RecipeSliceResponseDto readAllMyRecipesPopular(UserDetailsImpl userDetails, int pageNum, Boolean isPaid) {
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice;
        if(isPaid == null || !isPaid) {
            slice = recipeRepository.findPopularRecipesByUserId(userDetails.user().getId(), pageRequest);
        } else {
            slice = recipeRepository.findPopularRecipesByUserIdAndIsPaidTrue(userDetails.user().getId(), pageRequest);
        }
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, userDetails);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipeByFoodName(String keyword, int pageNum, UserDetailsImpl user){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.searchRecipesByFoodName(keyword, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto readAllRecipeByTitleOrNickname(String keyword, int pageNum, UserDetailsImpl user){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.searchRecipesByTitleOrNickname(keyword, pageRequest);
        List<RecipeReadAllServiceResponseDto> recipes = mappingRecipes(slice, user);
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceResponseDto filterRecipes(RecipeFilterConditionDto condition, int pageNum, UserDetailsImpl user){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findAllByFilter(condition, pageRequest);
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
        String fileUrl = requestDto.imageUrl();
        if (multipartFile == null || multipartFile.isEmpty()) {
            if (recipe.getImageUrl() != null && requestDto.imageUrl() == null) {
                s3Provider.delete(recipe.getImageUrl());
                fileUrl = null;
                folderName = null;
            }
        } else {
            fileUrl = s3Provider.updateImage(recipe.getImageUrl(), folderName, multipartFile);
        }

        recipe.updateRecipe(requestDto.title(), requestDto.intro(), folderName, fileUrl,
                requestDto.recipeCategoryList(), requestDto.price(), requestDto.cookingTime(),
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
        recipeMemoRepository.deleteAllByRecipeId(id);
        recipeRepository.delete(recipe);
        s3Provider.delete(recipe.getFolderName());
        user.updateRecipeCount(user.getRecipeCount() - 1);
        userRepository.save(user);
    }

    @Transactional
    public void deleteRecipeByAdmin(Long id, User user) {
        Recipe recipe = validateRecipe(id, user);
        recipeFoodRepository.deleteAllByRecipeId(id);
        recipeProcessRepository.deleteAllByRecipeId(id);
        recipeMemoRepository.deleteAllByRecipeId(id);
        recipeRepository.delete(recipe);
        s3Provider.delete(recipe.getFolderName());
        Long writerId = recipe.getUserId();
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        writer.updateRecipeCount(writer.getRecipeCount() - 1);
        userRepository.save(writer);
    }

    @Override
    public RecipeAllStatisticTypeDto readRecipeStatistics(){
        Long totalCount = recipeRepository.count();
        LocalDateTime startDate = LocalDate.now().minusDays(6).atStartOfDay();
        List<Object[]> results = recipeRepository.findDailyCreatedCount(startDate);
        List<RecipeStatisticDto> dailyCounts = results.stream()
                .map(row -> new RecipeStatisticDto(
                        ((java.sql.Date) row[0]).toLocalDate().toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
        RecipeAllStatisticDto recipeAllStatisticDto = new RecipeAllStatisticDto(totalCount, dailyCounts);
        return new RecipeAllStatisticTypeDto(recipeAllStatisticDto);
    }

    @Override
    public RecipeSliceByAdminResponseDto readAllRecipeByAdmin(int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.findAll(pageRequest);
        List<RecipeReadByAdminResponseDto> recipes = slice.getContent().stream()
                .map(recipe -> {
                    String userName = userRepository.findById(recipe.getUserId())
                            .map(User::getUserName)
                            .orElse("Unknown");
                    String nickname = userRepository.findById(recipe.getUserId())
                            .map(User::getNickname)
                            .orElse("Unknown");
                    return recipeEntityMapper.toRecipeReadByAdminResponseDto(recipe, userName, nickname);
                })
                .toList();
        return new RecipeSliceByAdminResponseDto(recipes, slice.hasNext());
    }

    @Override
    public RecipeSliceByAdminResponseDto searchRecipeByAdmin(String keyword, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeRepository.searchRecipesByTitleOrNickname(keyword, pageRequest);

        // 작성자 id
        List<Long> userIds = slice.getContent().stream()
                .map(Recipe::getUserId)
                .distinct()
                .toList();

        // 작성자 조회
        Map<Long, String> userIdToUserName = userRepository.findUserNamesByUserIds(userIds).stream()
                .collect(Collectors.toMap(RecipeUserNameDto::userId, RecipeUserNameDto::userName));

        // 작성자 닉네임 조회
        // 탈퇴한 사람이면 Unknown
        Map<Long, String> userIdToNickname = userRepository.findNicknamesByUserIds(userIds).stream()
                .collect(Collectors.toMap(RecipeUserNicknameDto::userId, RecipeUserNicknameDto::nickname));

        List<RecipeReadByAdminResponseDto> recipes = slice.getContent().stream()
                .map(recipe -> {
                    String userName = userIdToUserName.getOrDefault(recipe.getUserId(), "Unknown");
                    String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                    return recipeEntityMapper.toRecipeReadByAdminResponseDto(recipe, userName, nickname);
                })
                .collect(Collectors.toList());

        return new RecipeSliceByAdminResponseDto(recipes, slice.hasNext());
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
        // 탈퇴한 사람이면 Unknown
        Map<Long, String> userIdToNickname = userRepository.findNicknamesByUserIds(userIds).stream()
                .collect(Collectors.toMap(RecipeUserNicknameDto::userId, RecipeUserNicknameDto::nickname));

        // 좋아요 개수
        Map<Long, Long> likeCountMap = recipeLikeRepository.countLikesByRecipeIds(recipeIds).stream()
                .collect(Collectors.toMap(RecipeLikeCountDto::recipeId, RecipeLikeCountDto::likeCount));
        List<RecipeReadAllServiceResponseDto> recipes;

        //
        if(userDetails == null){
            recipes = slice.getContent().stream()
                    .map(recipe -> {
                        Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                        String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                        return recipeEntityMapper.toRecipeReadResponseDto(recipe, likeCount, false, nickname, false);
                    })
                    .collect(Collectors.toList());
        } else {
            // 저장 여부
            final Map<Long, Boolean> isSavedMap = (userDetails != null)
                    ? recipeSaveRepository.findSavesByRecipeAndUser(recipeIds, userDetails.user().getId())
                    .stream()
                    .collect(Collectors.toMap(
                            RecipeIsSavedDto::recipeId,
                            RecipeIsSavedDto::isSaved
                    ))
                    : Collections.emptyMap();
            // isWriter 위해
            Long userId = userDetails.user().getId();
            recipes = slice.getContent().stream()
                    .map(recipe -> {
                        Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                        Boolean isSaved = isSavedMap.getOrDefault(recipe.getId(), false);
                        String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                        Boolean isWriter = userId.equals(recipe.getUserId());
                        return recipeEntityMapper.toRecipeReadResponseDto(recipe, likeCount, isSaved, nickname, isWriter);
                    })
                    .collect(Collectors.toList());
        }
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

//    private Integer calculateCal(RecipeFoodCreateServiceRequestDto rfDto, Food food){
//        double userAmountInBase = rfDto.recipeFoodUnit().toBaseUnit(rfDto.amount());
//        double foodUnitToBase = food.getFoodUnit().toBaseUnit(1.0);
//        double caloriePerBaseUnit = food.getCal() / foodUnitToBase;
//        int cal = (int) (userAmountInBase * caloriePerBaseUnit);
//        return cal;
//    }
}
