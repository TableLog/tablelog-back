package com.tablelog.tablelogback.domain.recipe_process.dto.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public record RecipeProcessDto(
        short sequence,
        String rpTitle,
        String description,
        List<MultipartFile> files
){}