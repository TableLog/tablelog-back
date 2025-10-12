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
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "피드 생성")
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
    @PutMapping(value = "/boards/{board_id}" ,consumes  = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "피드 수정")
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
    @Operation(summary = "피드 전체 조회, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
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
    @Operation(summary = "피드 최신순 정렬, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
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
    @Operation(summary = "피드 오래된 순 정렬, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
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
    @Operation(summary = "피드 상세 조회")
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
    @GetMapping("/{user_id}/boards")
    @Operation(summary = "유저 기준 일기 조회")
    @Description("유저 기준으로 일기 조회, 유저 없으면 에러 출력")
    public ResponseEntity<BoardListResponseDto> readBoard(
        @PathVariable Long user_id,
        @RequestParam("page") Integer pageNumber
    ){
        BoardListResponseDto readResponseDto = boardService.getReadAllLoginUser(pageNumber,user_id);
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
