package com.tablelog.tablelogback.domain.board.service.impl;


import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.mapper.entity.BoardEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.s3.S3Provider;
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
    private final S3Provider s3Provider;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";


    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final BoardCreateServiceRequestDto boardRequestDto
            , User user
            , MultipartFile multipartFile
            )throws IOException
    {
        // Mapper로 만들기

        String fileName;
        String fileUrl;
        /* Builder로 만들기
            Test test = Test.builder()
            .name(testRequestDto.name())
            .age(testRequestDto.age())
            .build();
         */
        if (multipartFile.isEmpty()) {
            fileUrl = null;
            Board board = boardEntityMapper.toBoard(boardRequestDto,fileUrl
                    ,user
            );
            boardRepository.save(board);
        } else {
            fileName = s3Provider.originalFileName(multipartFile);
            fileUrl = url + user.getFolderName() + SEPARATOR + fileName;
            Board board = boardEntityMapper.toBoard(boardRequestDto,fileUrl
                    ,user);
            boardRepository.save(board);
            fileUrl = user.getFolderName() + SEPARATOR + fileName;
            s3Provider.saveFile(multipartFile, fileUrl);
        }
    }
    @Override
    public void update(final BoardUpdateServiceRequestDto boardRequestDto
            , User user
            , Long board_id
            , MultipartFile multipartFile
    )throws IOException
    {
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        String folderName = user.getFolderName();
        String fileName;
        String fileUrl;
        if (multipartFile.isEmpty()) {
            fileUrl = board.getImage_url();
            board.updateBoard(boardRequestDto.title(), boardRequestDto.content(),fileUrl,boardRequestDto.category().toString());
            boardRepository.save(board);
        } else {
            fileName = s3Provider.originalFileName(multipartFile);
            fileUrl = url + user.getFolderName() + SEPARATOR + fileName;
            board.updateBoard(boardRequestDto.title(), boardRequestDto.content(),fileUrl,boardRequestDto.category().toString());
            boardRepository.save(board);
            fileUrl = user.getFolderName() + SEPARATOR + fileName;
            s3Provider.saveFile(multipartFile, fileUrl);
        }
    }
    @DeleteMapping
    public void delete(Long board_id,User user){
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        if (board.getImage_url()==null){
            boardRepository.delete(board);
        }else {
            String image_name = board.getImage_url().replace(url,"");
            image_name = image_name.substring(image_name.lastIndexOf("/"));
            boardRepository.delete(board);
            s3Provider.delete(user.getFolderName()+image_name);
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
    public List<BoardReadResponseDto> getAll(int pageNumber) {
        Slice<Board> boards = boardRepository.findAllBy(PageRequest.of(pageNumber, 5));
        return boardEntityMapper.toBoardReadResponseDtos(boards.getContent());
    }
    @Override
    public  BoardReadResponseDto getOnce(Long id){
        Board board = boardRepository.findById(id)
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        return boardEntityMapper.toTestReadResponseDto(board);
    }
}
