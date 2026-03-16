package com.tablelog.tablelogback.domain.board.service.impl;

import com.tablelog.tablelogback.domain.board.dto.service.*;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.mapper.entity.BoardEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.domain.point_transaction.repository.PointTransactionRepository;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.PointReason;
import com.tablelog.tablelogback.global.enums.PointType;
import com.tablelog.tablelogback.global.s3.AsyncImageUploadService;
import com.tablelog.tablelogback.global.s3.S3Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardEntityMapper boardEntityMapper;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final S3Provider s3Provider;
    private final UserRepository userRepository;
    private final AsyncImageUploadService asyncImageUploadService;

    @Override
    public void createBoard(final BoardCreateServiceRequestDto boardRequestDto,
                            User user, List<MultipartFile> multipartFiles
    ) throws IOException {
        // ── 이미지 URL 선행 계산 (업로드는 비동기로)
        List<String> imageUrls = null;
        List<byte[]> imageBytesList = new ArrayList<>();
        List<String> contentTypes = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            imageUrls = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                if (!file.isEmpty()) {
                    String key = s3Provider.computeKey(file, user.getFolderName());
                    imageUrls.add(s3Provider.getImagePath(key));
                    keys.add(key);
                    imageBytesList.add(file.getBytes());
                    contentTypes.add(file.getContentType());
                }
            }
        }

        // ── DB 저장 (S3 업로드 대기 없이 즉시)
        Board board = boardEntityMapper.toBoard(boardRequestDto, imageUrls, user);
        boardRepository.save(board);
        user.addPointBalance(300);
        user.updateBoardCount(user.getBoardCount() + 1);
        userRepository.save(user);
        PointTransaction pointTransaction = PointTransaction.builder()
                .userId(user.getId())
                .amount(300)
                .pointReason(PointReason.피드등록)
                .pointType(PointType.EARN)
                .build();
        pointTransactionRepository.save(pointTransaction);

        // ── S3 업로드 완료 후 응답 (이미지 깨짐 방지)
        if (!imageBytesList.isEmpty()) {
            try {
                CompletableFuture<Void> uploadFuture =
                        asyncImageUploadService.uploadBoardImages(imageBytesList, contentTypes, keys);
                uploadFuture.get(); // 업로드 완료될 때까지 대기
            } catch (Exception e) {
                log.error("[BoardService] S3 업로드 실패. error: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void updateBoard(final BoardUpdateServiceRequestDto boardRequestDto,
                            User user, Long board_id, List<MultipartFile> multipartFiles
    ) throws IOException {
        User user1 = userRepository.findByNickname(user.getNickname())
            .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findByIdAndUser(board_id,user1.getNickname())
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        List<String> imageUrls;
        List<String> oldImageUrls = board.getImage_urls();
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            imageUrls = boardRequestDto.image_urls();
            for (String oldImageUrl : oldImageUrls) {
                if (!imageUrls.contains(oldImageUrl)) {
                    // 저장된 URL을 그대로 넘기면 S3Provider가 내부에서 경로를 추출하여 삭제 처리
                    s3Provider.delete(oldImageUrl);
                }
            }
            board.updateBoard(boardRequestDto.title(), boardRequestDto.content(),
                    imageUrls, boardRequestDto.category().toString());
            boardRepository.save(board);
        } else {
            imageUrls = boardRequestDto.image_urls();
            List<String> newImageUrls = s3Provider.updateImages(multipartFiles, user.getFolderName());
            imageUrls.addAll(newImageUrls);
            for (String oldImageUrl : oldImageUrls) {
                if (!imageUrls.contains(oldImageUrl)) {
                    // 저장된 URL을 그대로 넘기면 S3Provider가 내부에서 경로를 추출하여 삭제 처리
                    s3Provider.delete(oldImageUrl);
                }
            }
            board.updateBoard(boardRequestDto.title(), boardRequestDto.content(),
                    imageUrls, boardRequestDto.category().toString());
            boardRepository.save(board);
        }
    }
    @DeleteMapping
    public void deleteBoard(Long board_id, User user){
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
            .orElseThrow(() -> new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        user.updateBoardCount(user.getBoardCount() - 1);
        userRepository.save(user);
        if(board.getImage_urls() == null){
            boardRepository.delete(board);
        } else {
            for (String imageUrl : board.getImage_urls()) {
                // 저장된 URL을 그대로 넘기면 S3Provider가 내부에서 경로를 추출하여 삭제 처리
                s3Provider.delete(imageUrl);
            }
            boardRepository.delete(board);
        }
    }

    @Override
    public BoardListResponseDto readAllBoard(int pageNum) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdAsc(PageRequest.of(pageNum, 5));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(),
                    commentCount, likeCount,false,false, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @Override
    public BoardListResponseDto readAllBoardByDesc(int pageNum) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdDesc(PageRequest.of(pageNum, 5));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(),
                    commentCount, likeCount,false,false, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @Override
    public BoardListResponseDto readAllBoardByAsc(int pageNum) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdAsc(PageRequest.of(pageNum, 5));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(),
                    commentCount, likeCount,false,false, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @Override
    public  BoardReadResponseDto readBoard(Long boardId){
        Board board = boardRepository.findById(boardId)
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        User writer = userRepository.findByNickname(board.getUser())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Long likeCount = boardLikeRepository.countByBoard(boardId);
        Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
        return boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(), commentCount, likeCount,
                false,false,writer.getId());
    }

    @Override
    public BoardReadResponseDto readBoardByLogin(Long id, User user){
        Board board = boardRepository.findById(id)
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        User writer = userRepository.findByNickname(board.getUser())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Boolean isMe = board.getUser().equals(user.getNickname());
        Boolean isLike = boardLikeRepository.existsByBoardAndUser(board.getId(),user.getId());
        Long likeCount = boardLikeRepository.countByBoard(id);
        Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
        return boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(), commentCount, likeCount,
                isMe, isLike, writer.getId());
    }

    @Override
    public BoardListResponseDto readAllBoardByDescAndUser(int pageNum, User user) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdDesc(PageRequest.of(pageNum, 5));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            boolean isMe = board.getUser().equals(user.getNickname());
            boolean isLike = boardLikeRepository.existsByBoardAndUser(board.getId(), user.getId());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(), commentCount,
                    likeCount, isMe, isLike, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @Override
    public BoardListResponseDto readAllBoardByUser(int pageNum, User user) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdAsc(PageRequest.of(pageNum, 5));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            boolean isMe = board.getUser().equals(user.getNickname());
            boolean isLike = boardLikeRepository.existsByBoardAndUser(board.getId(),user.getId());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(), commentCount,
                    likeCount, isMe, isLike, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @Override
    public BoardListResponseDto readAllBoardByLoginUser(int pageNum, Long userId) {
        User user = userRepository.findById(userId).
            orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Slice<Board> boards = boardRepository.findAllByUserOrderByUserAsc(user.getNickname(),PageRequest.of(pageNum, 9));
        List<Board> boardList = boards.getContent();
        List<BoardReadResponseDto> responseDtos = new ArrayList<>();
        for (Board board : boardList) {
            User writer = userRepository.findByNickname(board.getUser())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            Long likeCount = boardLikeRepository.countByBoard(board.getId());
            Integer commentCount = boardCommentRepository.countByBoardId(board.getId().toString());
            boolean isMe = board.getUser().equals(user.getNickname());
            boolean isLike = boardLikeRepository.existsByBoardAndUser(board.getId(),user.getId());
            responseDtos.add(boardEntityMapper.toReadResponseDto(board, writer.getProfileImgUrl(), commentCount,
                    likeCount, isMe, isLike, writer.getId()));
        }
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }

    @DeleteMapping
    public void deleteBoardByAdmin(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        String nickname = board.getUser();
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if (board.getImage_urls() == null){
            boardRepository.delete(board);
        } else {
            for (String imageUrl : board.getImage_urls()) {
                // 저장된 URL을 그대로 넘기면 S3Provider가 내부에서 경로를 추출하여 삭제 처리
                s3Provider.delete(imageUrl);
            }
            boardRepository.delete(board);
        }
    }

    @Override
    public BoardAllStatisticTypeDto readBoardStatistics(){
        Long totalCount = boardRepository.count();
        LocalDateTime startDate = LocalDate.now().minusDays(6).atStartOfDay();
        List<Object[]> results = boardRepository.findDailyCreatedCount(startDate);
        List<BoardStatisticDto> dailyCounts = results.stream()
                .map(row -> new BoardStatisticDto(
                        ((java.sql.Date) row[0]).toLocalDate().toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
        BoardAllStatisticDto boardAllStatisticDto = new BoardAllStatisticDto(totalCount, dailyCounts);
        return new BoardAllStatisticTypeDto(boardAllStatisticDto);
    }

    @Override
    public BoardReadSliceByAdminDto readAllBoardByAdmin(int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Board> slice = boardRepository.findAll(pageRequest);
        List<BoardReadByAdminResponseDto> boards = slice.getContent().stream()
                .map(board -> {
                    User writer = userRepository.findByUserName(board.getUser())
                            .orElse(null);
                    String userName = "Unknown";
                    Long writerId = 0L;
                    if(writer != null){
                        userName = writer.getUserName();
                        writerId = writer.getId();
                    }
                    return boardEntityMapper.toRecipeReadByAdminResponseDto(board, userName, writerId);
                })
                .toList();
        return new BoardReadSliceByAdminDto(boards, slice.hasNext());
    }

    @Override
    public BoardReadSliceByAdminDto searchBoardByAdmin(String keyword, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<BoardReadByAdminResponseDto> slice = boardRepository.searchBoardsByUserNameOrNickname(keyword, pageRequest);
        return new BoardReadSliceByAdminDto(slice.getContent(), slice.hasNext());
    }
}
