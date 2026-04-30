package com.tablelog.tablelogback.global.exception.handler;
import java.util.List;
import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;
import com.tablelog.tablelogback.global.exception.dto.BeanValidationExceptionResponseDto;
import com.tablelog.tablelogback.global.exception.dto.CustomExceptionResponseDto;
import com.tablelog.tablelogback.global.exception.dto.FieldErrorResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden // 스웨거 인식
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionResponseDto> customExceptionHandler(
            CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.error("커스텀 예외 발생 {} {}: {}", exception.getClass().getSimpleName(), errorCode.name(),
                errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CustomExceptionResponseDto.builder()
                        .status(errorCode.getStatus().value())
                        .name(errorCode.name())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(
            BindingResult bindingResult) {
        List<FieldErrorResponseDto> fieldErrorResponseDtos = bindingResult.getFieldErrors().stream()
                .map(fieldError -> FieldErrorResponseDto.builder()
                        .name(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();
        log.error("Bean Validation 예외 발생: {}", fieldErrorResponseDtos);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BeanValidationExceptionResponseDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .messages(fieldErrorResponseDtos)
                        .build());
    }

    // 추가 1: NPE 등 서버 내부 오류가 401로 둔갑하는 것 방지
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomExceptionResponseDto> nullPointerExceptionHandler(
            NullPointerException exception) {
        log.error("NullPointerException 발생: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomExceptionResponseDto.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .name("NULL_POINTER_EXCEPTION")
                        .message("서버 내부 오류가 발생했습니다.")
                        .build());
    }

    // @CookieValue required = ture인데 없는 경우
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<CustomExceptionResponseDto> missingCookieHandler(
            MissingRequestCookieException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomExceptionResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .name("UNAUTHORIZED")
                        .message("로그인이 필요합니다.")
                        .build());
    }

    // 추가 2: 그 외 모든 예외 catch (최후 방어선)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomExceptionResponseDto> exceptionHandler(
            Exception exception) {
        log.error("예외 발생: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomExceptionResponseDto.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .name("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다.")
                        .build());
    }
}