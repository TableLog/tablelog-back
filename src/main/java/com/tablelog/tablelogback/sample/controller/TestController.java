package com.tablelog.tablelogback.sample.controller;



import java.util.List;

import com.tablelog.tablelogback.sample.dto.controller.TestCreateControllerRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestCreateServiceRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.sample.mapper.dto.TestDtoMapper;
import com.tablelog.tablelogback.sample.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

@RequestMapping("/api/v1")
@Tag(name = "알림 API", description = "컨트롤러에 대한 설명입니다.")
public class TestController {

    private final TestService testService;
    private final TestDtoMapper testDtoMapper;

    @GetMapping("/tests/{testId}")
    public ResponseEntity<TestReadResponseDto> get(
        @PathVariable(name = "testId") Long testId
    ) {
        TestReadResponseDto responseDto = testService.get(testId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/tests")
    public ResponseEntity<List<TestReadResponseDto>> getAll(
        @RequestParam("page") Integer pageNumber
    ) {
        List<TestReadResponseDto> responseDto = testService.getAll(pageNumber);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/tests")
    public ResponseEntity<Void> create(
        @RequestBody TestCreateControllerRequestDto controllerRequestDto
    ) {
        TestCreateServiceRequestDto serviceRequestDto = testDtoMapper.toTestServiceRequestDto(
            controllerRequestDto);
        testService.create(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
