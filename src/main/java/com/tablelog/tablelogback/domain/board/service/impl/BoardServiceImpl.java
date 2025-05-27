package com.tablelog.tablelogback.domain.board.service.impl;


import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.mapper.entity.BoardEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.s3.S3Provider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardEntityMapper boardEntityMapper;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final S3Provider s3Provider;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    private final UserRepository userRepository;
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";


    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final BoardCreateServiceRequestDto boardRequestDto
            , User user
            , List<MultipartFile> multipartFiles
            )throws IOException
    {
        List<String> imageUrls;
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            imageUrls = null;
        } else {
            imageUrls = s3Provider.updateImages(multipartFiles, user.getFolderName());
        }
        Board board = boardEntityMapper.toBoard(boardRequestDto, imageUrls, user);
        boardRepository.save(board);
        user.addPointBalance(300);
        userRepository.save(user);
    }
    @Override
    public void update(final BoardUpdateServiceRequestDto boardRequestDto
            , User user
            , Long board_id
            , List<MultipartFile> multipartFiles
    )throws IOException
    {
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        List<String> imageUrls;
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            imageUrls = board.getImage_urls();
            System.out.println(imageUrls);
        } else {
            imageUrls = s3Provider.updateImages(multipartFiles, user.getFolderName());
        }
        board.updateBoard(boardRequestDto.title(), boardRequestDto.content(), imageUrls, boardRequestDto.category().toString());
        boardRepository.save(board);
    }
    @DeleteMapping
    public void delete(Long board_id,User user){
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        if (board.getImage_urls()==null){
            boardRepository.delete(board);
        }else {
            for (String imageUrl : board.getImage_urls()) {
                String image_name = imageUrl.replace(url,"");
                image_name = image_name.substring(image_name.lastIndexOf("/"));
                s3Provider.delete(user.getFolderName()+image_name);
            }
            boardRepository.delete(board);
        }
    }
    // Test -> TestCreateServiceRequestDto
//    @Override
//    public TestReadResponseDto get(Long id) {
//        Board board = boardRepository.findById(id)
//            .orElseThrow(() -> new NotFoundTestException(BoardErrorCode.NOT_FOUND_BOARD));
//        return boardEntityMapper.toTestReadResponseDto(board);
//    }
//
//    // List<Test> -> List<TestCreateServiceRequestDto>
    @Override
    public BoardListResponseDto getAll(int pageNumber) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdDesc(PageRequest.of(pageNumber, 5));
        List<BoardReadResponseDto> responseDtos = boardEntityMapper.toBoardReadResponseDtos(boards.getContent());
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }
    @Override
    public BoardListResponseDto getAllByDesc(int pageNumber) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdDesc(PageRequest.of(pageNumber, 5));
        List<BoardReadResponseDto> responseDtos = boardEntityMapper.toBoardReadResponseDtos(boards.getContent());
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }
    @Override
    public BoardListResponseDto getAllByAsc(int pageNumber) {
        Slice<Board> boards = boardRepository.findAllByOrderByIdAsc(PageRequest.of(pageNumber, 5));
        List<BoardReadResponseDto> responseDtos = boardEntityMapper.toBoardReadResponseDtos(boards.getContent());
        return new BoardListResponseDto(responseDtos, boards.hasNext());
    }
    @Override
    public  BoardReadResponseDto getOnce(Long id){
        Board board = boardRepository.findById(id)
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        User user = userRepository.findByNickname(board.getUser()
        ).orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Long like_count = boardLikeRepository.countByBoard(id);
        Integer comment_count = boardCommentRepository.countByBoardId(board.getId().toString());
        return boardEntityMapper.toReadResponseDto(board,user,comment_count,like_count);
    }
}
