package com.tablelog.tablelogback.domain.recipe_memo.service;

import com.tablelog.tablelogback.domain.user.entity.User;

public interface RecipeMemoService {
    void createRecipeMemo(Long recipeId, User user, String memo);
}
