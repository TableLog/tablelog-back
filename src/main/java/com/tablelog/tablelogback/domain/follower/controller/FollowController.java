package com.tablelog.tablelogback.domain.follower.controller;

import com.tablelog.tablelogback.domain.follower.service.impl.FollowServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "언팔로우")
    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<?> deleteFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        followService.deleteFollow(userId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "팔로우 단건 조회", description = "내가 이 사람을 팔로우하는지")
    @GetMapping("/users/{userId}/follow/me")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(followService.isFollowing(userId, userDetails.user()));
    }
}
