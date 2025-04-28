package com.tablelog.tablelogback.domain.recipe_process.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class RecipeProcessDto {
    private short sequence;
    private String rpTitle;
    private String description;
    private List<MultipartFile> files;
}