package com.tablelog.tablelogback.domain.board_comment.controller;

import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentListResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.mapper.dto.BoardCommentDtoMapper;
import com.tablelog.tablelogback.domain.board_comment.service.BoardCommentService;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 댓글 API", description = "게시판에 대한 댓글 CRUD")
public class BoardCommentController {
    private final BoardCommentDtoMapper boardCommentDtoMapper;
    private final BoardCommentService boardCommentService;

    @Operation(summary = "피드댓글 생성")
    @PostMapping("/boards/{boardId}/board_comments")
    public ResponseEntity<?> createBoardComment(
            @PathVariable Long boardId,
            @RequestBody BoardCommentCreateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        BoardCommentCreateServiceRequestDto boardCommentCreateServiceRequestDto
                = boardCommentDtoMapper.toBoardCommentServiceRequestDto(requestDto);
        boardCommentService.createBoardComment(
                boardCommentCreateServiceRequestDto, boardId, userDetails.user(),null);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "피드답글 생성")
    @PostMapping("/boards/{boardId}/board_comments/{boardCommentId}")
    public ResponseEntity<?> createCommentReply(
        @PathVariable Long boardId,
        @PathVariable Long boardCommentId,
        @RequestBody BoardCommentCreateControllerRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        BoardCommentCreateServiceRequestDto boardCommentCreateServiceRequestDto
                = boardCommentDtoMapper.toBoardCommentServiceRequestDto(requestDto);
        boardCommentService.createBoardComment(
                boardCommentCreateServiceRequestDto, boardId, userDetails.user(), boardCommentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "피드댓글 수정")
    @PutMapping("/boards/{boardId}/board_comments/{boardCommentId}")
    public ResponseEntity<?> updateBoardComment(
            @PathVariable Long boardId,
            @PathVariable Long boardCommentId,
            @RequestBody BoardCommentUpdateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException{
        BoardCommentUpdateServiceRequestDto boardCommentUpdateServiceRequestDto
                = boardCommentDtoMapper.toBoardCommentUpdateServiceRequestDto(requestDto);
        boardCommentService.updateBoardComment(
                boardCommentUpdateServiceRequestDto, userDetails.user(), boardId, boardCommentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "피드댓글 삭제")
    @DeleteMapping("/boards/{boardId}/board_comments/{boardCommentId}")
    public ResponseEntity<?> deleteBoardComment(
            @PathVariable Long boardId,
            @PathVariable Long boardCommentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException{
        boardCommentService.deleteBoardComment(boardId, boardCommentId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "피드댓글 단건 조회")
    @GetMapping("/boards/{boardId}/board_comments/{boardCommentId}")
    public ResponseEntity<BoardCommentReadResponseDto> readBoardComment(
            @PathVariable Long boardId,
            @PathVariable Long boardCommentId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardCommentService.readBoardComment(boardId, boardCommentId));
    }

    @Operation(summary = "피드댓글 전체 조회 내림차순")
    @GetMapping("/boards/{boardId}/board_comments/desc")
    public ResponseEntity<BoardCommentListResponseDto> readAllByDescBoardComments(
            @PathVariable Long boardId,
            @RequestParam("page") Integer pageNum
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardCommentService.readAllBoardCommentByDesc(boardId, pageNum));
    }

    @Operation(summary = "피드댓글 전체 조회")
    @GetMapping("/boards/{boardId}/board_comments")
    public ResponseEntity<BoardCommentListResponseDto> readAllBoardComments(
        @RequestParam("page") Integer pageNum,
        @PathVariable Long boardId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(boardCommentService.readAllBoardComment(boardId, pageNum));
    }
}
