package com.tablelog.tablelogback.domain.board_comment.controller;


import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.mapper.dto.BoardCommentDtoMapper;
import com.tablelog.tablelogback.domain.board_comment.service.BoardCommentService;
import com.tablelog.tablelogback.global.enums.BoardCategory;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 댓글 API", description = "게시판에 대한 댓글 CRUD")
public class BoardCommentController {
    private final BoardCommentDtoMapper boardCommentDtoMapper;
    private final BoardCommentService boardCommentService;

    @PostMapping("boards/{board_id}/board_comment")
    public ResponseEntity<?> createBoard(
            @PathVariable Long board_id,
            @RequestBody BoardCommentCreateControllerRequestDto requestDto,
            // BoardCreateControllerRequestDto controllerRequestDto
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        BoardCommentCreateServiceRequestDto boardCommentCreateServiceRequestDto = boardCommentDtoMapper.toBoardCommentServiceRequestDto(requestDto);
        boardCommentService.create(boardCommentCreateServiceRequestDto,board_id,userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("/boards/{board_id}/board_comments/{board_comment_id}")
    public ResponseEntity<?> updateBoardComment(
            @PathVariable Long board_id,
            @PathVariable Long board_comment_id,
            @RequestBody BoardCommentUpdateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException{
        BoardCommentUpdateServiceRequestDto boardCommentUpdateServiceRequestDto = boardCommentDtoMapper.toBoardCommentUpdateServiceRequestDto(requestDto);
        boardCommentService.update(boardCommentUpdateServiceRequestDto,userDetails.user(),board_id,board_comment_id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/boards/{board_id}/board_comments/{board_comment_id}")
    public void deleteBoardComment(
            @PathVariable Long board_id,
            @PathVariable Long board_comment_id,
            String user
    )throws IOException{

    }
    @GetMapping("/boards/{board_id}/board_comments")
    public Slice<BoardCommentReadResponseDto> readAllBoardComments(
            @PathVariable Long board_id,
            @RequestParam int pageNumber
    ) {
        return boardCommentService.getAll(board_id,pageNumber);
    }

//    @GetMapping("/boards/{board_id}/board_comments/{board_comment_id}")
//    public Map<String, Object> readAllBoards(
//            @PathVariable Long board_id,
//            @PathVariable Long board_comment_id
//    )throws  IOException{
//        Map<String, Object> board_comment1 = new HashMap<>();
//        board_comment1.put("content", "이것은 첫 번째 게시글의 내용입니다.");
//        board_comment1.put("user", "admin");
//        return board_comment1;
//    }
}
