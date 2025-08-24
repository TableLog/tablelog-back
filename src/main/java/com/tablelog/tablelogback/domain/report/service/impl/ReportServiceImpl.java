package com.tablelog.tablelogback.domain.report.service.impl;

import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_comment.exception.BoardCommentErrorCode;
import com.tablelog.tablelogback.domain.board_comment.exception.NotFoundBoardCommentException;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_review.exception.NotFoundRecipeReviewException;
import com.tablelog.tablelogback.domain.recipe_review.exception.RecipeReviewErrorCode;
import com.tablelog.tablelogback.domain.recipe_review.repository.RecipeReviewRepository;
import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportReadResponseDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportSliceResponseDto;
import com.tablelog.tablelogback.domain.report.entity.Report;
import com.tablelog.tablelogback.domain.report.exception.NotFoundReportException;
import com.tablelog.tablelogback.domain.report.exception.ReportErrorCode;
import com.tablelog.tablelogback.domain.report.mapper.entity.ReportEntityMapper;
import com.tablelog.tablelogback.domain.report.repository.ReportRepository;
import com.tablelog.tablelogback.domain.report.service.ReportService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;
    private final ReportEntityMapper reportEntityMapper;
    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final RecipeRepository recipeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final RecipeReviewRepository recipeReviewRepository;

    @Override
    public void createReport(ReportCreateServiceRequestDto serviceRequestDto, User user){
        if(!userRepository.existsById(serviceRequestDto.reportedUserId())){
            throw new NotFoundUserException(UserErrorCode.NOT_FOUND_USER);
        }
        validateTargetExists(serviceRequestDto.reportType(), serviceRequestDto.targetId());
        Report report = reportEntityMapper.toReport(serviceRequestDto, user.getId());
        reportRepository.save(report);
    }

    @Override
    public ReportReadResponseDto readReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        return reportEntityMapper.toReportReadResponseDto(report);
    }

    @Override
    public ReportSliceResponseDto readAllReports(ApplyStatus status, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Report> slice = null;
        if(status == null){
            slice = reportRepository.findAll(pageRequest);
        } else {
            slice = reportRepository.findAllByStatus(status, pageRequest);
        }
        List<ReportReadResponseDto> reports = reportEntityMapper.toReportReadAllResponseDto(slice.getContent());
        return new ReportSliceResponseDto(reports, slice.hasNext());
    }

    @Override
    public void approveReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        report.approve();
        reportRepository.save(report);
    }

    @Override
    public void rejectReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        report.reject();
        reportRepository.save(report);
    }

    @Override
    public void processingReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        validateTargetExists(report.getReportType(), report.getTargetId());
        report.processing();
        reportRepository.save(report);
    }

    private void validateTargetExists(ReportType type, Long id) {
        switch (type) {
            case R_USER -> userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            case R_BOARD -> boardRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
            case R_RECIPE -> recipeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
            case R_BOARD_COMMENT -> boardCommentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
            case R_RECIPE_REVIEW -> recipeReviewRepository.findById(id)
                    .orElseThrow(() -> new NotFoundRecipeReviewException(RecipeReviewErrorCode.NOT_FOUND_RECIPE_REVIEW));
        }
    }
}
