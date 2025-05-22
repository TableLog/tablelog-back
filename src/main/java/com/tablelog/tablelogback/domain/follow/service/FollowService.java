package com.tablelog.tablelogback.domain.follow.service;

import com.tablelog.tablelogback.domain.follow.dto.FollowUserListDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface FollowService {
    void createFollow(Long followingId, User user);
    void deleteFollow(Long followingId, User user);
    Boolean isFollowing(Long follwingId, User user);
    Long getFollowerCountByUser(Long userId);
    Long getFollowingCountByUser(Long userId);
    FollowUserListDto getFollowers(Long userId, int pageNum);
    FollowUserListDto getFollowings(Long userId, int pageNum);
}
