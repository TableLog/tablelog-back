package com.tablelog.tablelogback.domain.board.service;

import com.tablelog.tablelogback.domain.board.dto.service.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    void createBoard(BoardCreateServiceRequestDto requestDto, User user,
                List<MultipartFile> multipartFiles) throws IOException;
    void updateBoard(BoardUpdateServiceRequestDto requestDto, User user,
                Long id, List<MultipartFile> multipartFiles) throws IOException;
     void deleteBoard(Long id, User user) throws IOException;
    BoardListResponseDto readAllBoard(int pageNum);
    BoardListResponseDto readAllBoardByDesc(int pageNum);
    BoardListResponseDto readAllBoardByAsc(int pageNum);
    BoardReadResponseDto readBoard(Long boardId);
    BoardListResponseDto readAllBoardByUser(int pageNum, User user);
    BoardListResponseDto readAllBoardByDescAndUser(int pageNum, User user);
    BoardReadResponseDto readBoardByLogin(Long boardId, User user);
    BoardListResponseDto readAllBoardByLoginUser(int pageNum, Long user_id);
    void deleteBoardByAdmin(Long boardId);
    BoardAllStatisticTypeDto readBoardStatistics();
    BoardReadSliceByAdminDto readAllBoardByAdmin(int pageNum);
    BoardReadSliceByAdminDto searchBoardByAdmin(String keyword, int pageNum);
}
