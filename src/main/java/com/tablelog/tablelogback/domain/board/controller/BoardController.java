package com.tablelog.tablelogback.domain.board.controller;

import com.tablelog.tablelogback.domain.board.dto.controller.BoardCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.controller.BoardUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.*;
import com.tablelog.tablelogback.domain.board.mapper.dto.BoardDtoMapper;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "게시판 API", description = "")
public class BoardController {
    private final BoardService boardService;
    private final BoardDtoMapper boardDtoMapper;

    @Operation(summary = "피드 생성")
    @PostMapping(value = "/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart BoardCreateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
        BoardCreateServiceRequestDto boardCreateServiceRequestDto =
                boardDtoMapper.toBoardServiceRequestDto(controllerRequestDto);
        boardService.createBoard(boardCreateServiceRequestDto, userDetails.user(), multipartFiles);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "피드 수정")
    @PutMapping(value = "/boards/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId,
            @RequestPart BoardUpdateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
        BoardUpdateServiceRequestDto boardUpdateServiceRequestDto =
                boardDtoMapper.toBoardUpdateServiceRequestDto(controllerRequestDto);
        boardService.updateBoard(boardUpdateServiceRequestDto, userDetails.user(), boardId, multipartFiles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "피드 삭제")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        boardService.deleteBoard(boardId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "피드 전체 조회, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
    @GetMapping("/boards")
    public ResponseEntity<BoardListResponseDto> readAllBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("page") Integer pageNum
    ) {
        if (userDetails == null) {
            BoardListResponseDto responseDto = boardService.readAllBoard(pageNum);
            return ResponseEntity.ok(responseDto);
        }
        BoardListResponseDto responseDto = boardService.readAllBoardByUser(pageNum, userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "피드 최신순 정렬, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
    @GetMapping("/boards/desc")
    public ResponseEntity<BoardListResponseDto> readAllBoardByDesc(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("page") Integer pageNum
    ) {
        if (userDetails == null) {
            BoardListResponseDto responseDto = boardService.readAllBoardByDesc(pageNum);
            return ResponseEntity.ok(responseDto);
        }
        BoardListResponseDto responseDto = boardService.readAllBoardByDescAndUser(pageNum, userDetails.user());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "피드 오래된 순 정렬, 로그인한 사용자가 없을 경우 isMe, isLike null 처리")
    @GetMapping("/boards/asc")
    public ResponseEntity<BoardListResponseDto> readAllBoardByAsc(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("page") Integer pageNum
    ) {
        if (userDetails == null) {
            BoardListResponseDto responseDto = boardService.readAllBoardByAsc(pageNum);
            return ResponseEntity.ok(responseDto);
        } else {
            BoardListResponseDto responseDto = boardService.readAllBoardByUser(pageNum, userDetails.user());
            return ResponseEntity.ok(responseDto);
        }
    }

    @Operation(summary = "피드 단건 조회")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardReadResponseDto> readBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        BoardReadResponseDto responseDto = null;
        if (userDetails == null) {
            responseDto = boardService.readBoard(boardId);
        } else {
            responseDto = boardService.readBoardByLogin(boardId, userDetails.user());
        }
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "유저 기준 피드 조회", description = "유저 없으면 에러")
    @GetMapping("/{userId}/boards")
    public ResponseEntity<BoardListResponseDto> readAllBoardByLoginUser(
            @PathVariable Long userId,
            @RequestParam("page") Integer pageNum
    ) {
        BoardListResponseDto responseDto = boardService.readAllBoardByLoginUser(pageNum, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자 - 보드 강제 삭제")
    @DeleteMapping("/admin/boards/{boardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBoardByAdmin(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        boardService.deleteBoardByAdmin(boardId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "보드 통계 조회")
    @GetMapping("/admin/boards/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BoardAllStatisticTypeDto> readBoardStatistics(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        BoardAllStatisticTypeDto responseDto = boardService.readBoardStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자가 보드 전체 조회", description = "writerId 0은 탈퇴한 유저를 의미")
    @GetMapping("/admin/boards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BoardReadSliceByAdminDto> readAllBoardByAdmin(
            @RequestParam("page") Integer pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        BoardReadSliceByAdminDto responseDto = boardService.readAllBoardByAdmin(pageNum);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자가 보드 검색", description = "작성자(유저네임) / 닉네임")
    @GetMapping("/admin/boards/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BoardReadSliceByAdminDto> searchBoardByAdmin(
            @RequestParam String keyword,
            @RequestParam("page") Integer pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        BoardReadSliceByAdminDto responseDto = boardService.searchBoardByAdmin(keyword, pageNum);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
