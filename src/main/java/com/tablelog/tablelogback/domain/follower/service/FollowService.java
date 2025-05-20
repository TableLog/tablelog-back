package com.tablelog.tablelogback.domain.follower.service;

import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface FollowService {
    void createFollow(Long followingId, User user);
    void deleteFollow(Long followingId, User user);
    Boolean isFollowing(Long follwingId, User user);
    Long getFollowerCountByUser(Long userId);
    Long getFollowingCountByUser(Long userId);
//    UserLoginResponseDto getFollowers(Long userId, int pageNum);
}
