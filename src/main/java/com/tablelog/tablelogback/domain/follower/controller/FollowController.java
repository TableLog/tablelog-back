package com.tablelog.tablelogback.domain.follower.controller;

import com.tablelog.tablelogback.domain.follower.service.impl.FollowServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Tag(name = "팔로우 API", description = "")
public class FollowController {
    private final FollowServiceImpl followService;

    @Operation(summary = "팔로우")
    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<?> addFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        followService.createFollow(userId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
