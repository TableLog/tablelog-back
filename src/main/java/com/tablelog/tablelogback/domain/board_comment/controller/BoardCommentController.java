package com.tablelog.tablelogback.domain.board_comment.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 댓글 API", description = "게시판에 대한 댓글 CRUD")
public class BoardCommentController {

    @PostMapping("boards/{board_id}/create")
    public void createBoard(
            @PathVariable Long board_id,
            String content
            // BoardCreateControllerRequestDto controllerRequestDto
            // @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {

    }
}
