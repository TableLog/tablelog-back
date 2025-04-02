package com.tablelog.tablelogback.domain.board_comment.controller;


import com.tablelog.tablelogback.global.enums.BoardCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
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

    @PostMapping("boards/{board_id}/board_comment")
    public void createBoard(
            @PathVariable Long board_id,
            String content,
            String user
            // BoardCreateControllerRequestDto controllerRequestDto
            // @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {

    }
    @PutMapping("/boards/{board_id}/board_comments/{board_comment_id}")
    public void updateBoardComment(
            @PathVariable Long board_id,
            @PathVariable Long board_comment_id,
            String content,
            String user
    )throws IOException{

    }
    @DeleteMapping("/boards/{board_id}/board_comments/{board_comment_id}")
    public void deleteBoardComment(
            @PathVariable Long board_id,
            @PathVariable Long board_comment_id,
            String user
    )throws IOException{

    }
    @GetMapping("/boards/{board_id}/board_comments")
    public List<Map<String, Object>> readAllBoardComments(
            @PathVariable Long board_id
    ) {
        List<Map<String, Object>> board_comments = new ArrayList<>();

        Map<String, Object> board_comment1 = new HashMap<>();
        board_comment1.put("content", "이것은 첫 번째 게시글의 댓글입니다.");
        board_comment1.put("user", "admin");

        Map<String, Object> board_comment2 = new HashMap<>();
        board_comment2.put("content", "이것은 두 번째 게시글의 내용입니다.");
        board_comment2.put("user", "user1");

        board_comments.add(board_comment1);
        board_comments.add(board_comment2);

        return board_comments;
    }

    @GetMapping("/boards/{board_id}/board_comments/{board_comment_id}")
    public Map<String, Object> readAllBoards(
            @PathVariable Long board_id,
            @PathVariable Long board_comment_id
    )throws  IOException{
        Map<String, Object> board_comment1 = new HashMap<>();
        board_comment1.put("content", "이것은 첫 번째 게시글의 내용입니다.");
        board_comment1.put("user", "admin");
        return board_comment1;
    }
}
