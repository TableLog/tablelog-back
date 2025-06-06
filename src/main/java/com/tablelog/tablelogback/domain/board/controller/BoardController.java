package com.tablelog.tablelogback.domain.board.controller;


import com.tablelog.tablelogback.domain.board.dto.controller.BoardCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.controller.BoardUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.mapper.dto.BoardDtoMapper;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.BoardCategory;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import com.tablelog.tablelogback.sample.dto.service.TestReadResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Description;
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
            @RequestPart(value = "multipartFiles",required = false) List<MultipartFile> multipartFiles
    )  throws IOException{
        BoardCreateServiceRequestDto boardCreateServiceRequestDto =
                boardDtoMapper.toBoardServiceRequestDto(controllerRequestDto);
        boardService.create(boardCreateServiceRequestDto,userDetails.user(),multipartFiles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("/boards/{board_id}")
    public ResponseEntity<?> updateBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long board_id,
            @RequestPart BoardUpdateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFiles",required = false) List<MultipartFile> multipartFiles
    )throws IOException{
        BoardUpdateServiceRequestDto boardUpdateServiceRequestDto =
                boardDtoMapper.toBoardUpdateServiceRequestDto(controllerRequestDto);
        boardService.update(boardUpdateServiceRequestDto,userDetails.user(),board_id,multipartFiles);
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
    public ResponseEntity<BoardListResponseDto> getAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("page") Integer pageNumber
    ) {
        if(userDetails==null){
            BoardListResponseDto responseDto = boardService.getAll(pageNumber);
            return ResponseEntity.ok(responseDto);
        }
        BoardListResponseDto responseDto = boardService.getAllByUser(pageNumber,userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/boards/desc")
    public ResponseEntity<BoardListResponseDto> getAllByDesc(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("page") Integer pageNumber
    ) {
        if (userDetails==null){
            BoardListResponseDto responseDto = boardService.getAllByDesc(pageNumber);
            return ResponseEntity.ok(responseDto);
        }
        BoardListResponseDto responseDto = boardService.getAllByDescAndUser(pageNumber,userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/boards/asc")
    public ResponseEntity<BoardListResponseDto> getAllByAsc(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("page") Integer pageNumber
    ) {
        if (userDetails==null){
            BoardListResponseDto responseDto = boardService.getAllByAsc(pageNumber);
            return ResponseEntity.ok(responseDto);
        }
        BoardListResponseDto responseDto = boardService.getAllByUser(pageNumber,userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/boards/{board_id}")
    public ResponseEntity<BoardReadResponseDto> readBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long board_id
    ){
        if (userDetails==null){
            BoardReadResponseDto responseDto = boardService.getOnce(board_id);
            return ResponseEntity.ok(responseDto);
        }
        BoardReadResponseDto readResponseDto = boardService.getOnceLogin(board_id,userDetails.user());
        return ResponseEntity.ok(readResponseDto);
    }

//    @GetMapping("/boards/user")
//    @Description("")
//    public ResponseEntity<BoardListResponseDto> getAllAndUser(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @RequestParam("page") Integer pageNumber
//    ) {
//        BoardListResponseDto responseDto = boardService.getAllByUser(pageNumber,userDetails.user());
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/boards/desc/user")
//    public ResponseEntity<BoardListResponseDto> getAllByDescAndUser(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @RequestParam("page") Integer pageNumber
//    ) {
//        BoardListResponseDto responseDto = boardService.getAllByDescAndUser(pageNumber,userDetails.user());
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/boards/{board_id}/user")
//    public ResponseEntity<BoardReadResponseDto> readAllBoardAndUser(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long board_id
//    ){
//        BoardReadResponseDto readResponseDto = boardService.getOnceLogin(board_id,userDetails.user());
//        return ResponseEntity.ok(readResponseDto);
//    }

    @GetMapping("/boards/me")
    public String ReadUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return userDetails.user().getNickname();
    }
}
