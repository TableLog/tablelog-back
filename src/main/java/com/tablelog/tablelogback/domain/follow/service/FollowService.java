package com.tablelog.tablelogback.domain.follow.service;

import com.tablelog.tablelogback.domain.follow.dto.FollowUserListDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;

public interface FollowService {
    void createFollow(Long followingId, User user);
    void deleteFollow(Long followingId, User user);
    Boolean isFollowing(Long followingId, User user);
    Long readFollowerCountByUser(Long userId);
    Long readFollowingCountByUser(Long userId);
    FollowUserListDto readFollowers(Long userId, int pageNum, UserDetailsImpl userDetails);
    FollowUserListDto readFollowings(Long userId, int pageNum, UserDetailsImpl userDetails);
}
