package com.tablelog.tablelogback.domain.board.controller;


import com.tablelog.tablelogback.domain.board.dto.controller.BoardCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.controller.BoardUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.mapper.dto.BoardDtoMapper;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.global.enums.BoardCategory;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "게시판 API", description = "")
public class BoardController {
    private final BoardService boardService;
    private final BoardDtoMapper boardDtoMapper;

    @PostMapping(value = "/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart BoardCreateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFile",required = false) MultipartFile multipartFile
    )  throws IOException{
        BoardCreateServiceRequestDto boardCreateServiceRequestDto =
                boardDtoMapper.toBoardServiceRequestDto(controllerRequestDto);
        boardService.create(boardCreateServiceRequestDto,userDetails.user(),multipartFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("/boards/{board_id}")
    public ResponseEntity<?> updateBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long board_id,
            @RequestPart BoardUpdateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFile",required = false) MultipartFile multipartFile
    )throws IOException{
        BoardUpdateServiceRequestDto boardUpdateServiceRequestDto =
                boardDtoMapper.toBoardUpdateServiceRequestDto(controllerRequestDto);
        boardService.update(boardUpdateServiceRequestDto,userDetails.user(),board_id,multipartFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/boards/{board_id}")
    public ResponseEntity<?> deleteBoard(
        @PathVariable Long board_id,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    )throws IOException{
        boardService.delete(board_id,userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/boards")
    public List<Map<String, Object>> readAllBoards() {
        List<Map<String, Object>> boards = new ArrayList<>();

        Map<String, Object> board1 = new HashMap<>();
        board1.put("title", "첫 번째 게시글");
        board1.put("content", "이것은 첫 번째 게시글의 내용입니다.");
        board1.put("category", "공지사항");
        board1.put("image_url", "https://example.com/image1.jpg");
        board1.put("user", "admin");

        Map<String, Object> board2 = new HashMap<>();
        board2.put("title", "두 번째 게시글");
        board2.put("content", "이것은 두 번째 게시글의 내용입니다.");
        board2.put("category", "자유게시판");
        board2.put("image_url", "https://example.com/image2.jpg");
        board2.put("user", "user1");

        boards.add(board1);
        boards.add(board2);

        return boards;
    }

    @GetMapping("/boards/{board_id}")
    public Map<String, Object> readAllBoards(
            @PathVariable Long board_id
    )throws  IOException{
        Map<String, Object> board1 = new HashMap<>();
        board1.put("title", "첫 번째 게시글");
        board1.put("content", "이것은 첫 번째 게시글의 내용입니다.");
        board1.put("category", "공지사항");
        board1.put("image_url", "https://example.com/image1.jpg");
        board1.put("user", "admin");
        return board1;
    }
}
