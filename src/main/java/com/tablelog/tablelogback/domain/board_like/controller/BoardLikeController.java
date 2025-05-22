package com.tablelog.tablelogback.domain.board_like.controller;

import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;
import com.tablelog.tablelogback.domain.board_like.service.impl.BoardLikeServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        boardLikeService.createBoardLike(boardId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "좋아요 삭제")
    @DeleteMapping("/boards/{boardId}/likes")
    public ResponseEntity<?> deleteBoardLike(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        boardLikeService.deleteBoardLike(boardId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "좋아요 단건 조회", description = "내가 눌렀는지?")
    @GetMapping("/boards/{boardId}/likes/me")
    public ResponseEntity<Boolean> hasBoardLiked(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(boardLikeService.hasBoardLiked(boardId, userDetails.user().getId()));
    }

    @Operation(summary = "좋아요 수 전체 조회 By 게시판")
    @GetMapping("/boards/{boardId}/likes/count")
    public ResponseEntity<Long> getBoardLikeCount(
            @PathVariable Long boardId
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(boardLikeService.getBoardLikeCountByBoard(boardId));
    }

    @Operation(summary = "내 좋아요 게시판 전체 조회")
    @GetMapping("/users/me/board-likes")
    public ResponseEntity<BoardListResponseDto> getMyLikedBoards(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int pageNumber
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(boardLikeService.getMyLikedBoards(userDetails.user().getId(), pageNumber));
    }
}
