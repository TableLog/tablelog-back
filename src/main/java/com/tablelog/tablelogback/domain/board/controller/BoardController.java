package com.tablelog.tablelogback.domain.board.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 API", description = "")
public class BoardController {

    @PostMapping("/board")
    public void createBoard(
            String title,
            String content,
            String category,
            MultipartFile image_file
            // BoardCreateControllerRequestDto controllerRequestDto
            // @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {

    }
}
