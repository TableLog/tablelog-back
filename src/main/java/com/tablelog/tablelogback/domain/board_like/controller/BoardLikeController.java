package com.tablelog.tablelogback.domain.board_like.controller;

import com.tablelog.tablelogback.domain.board_like.service.impl.BoardLikeServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Tag(name = "게시판 좋아요 API", description = "")
public class BoardLikeController {
    private final BoardLikeServiceImpl boardLikeService;

    @Operation(summary = "좋아요 생성")
    @PostMapping("/boards/{boardId}/likes")
    public ResponseEntity<?> addBoardLike(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        boardLikeService.createLike(boardId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
