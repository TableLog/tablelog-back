package com.tablelog.tablelogback.domain.follower.service.impl;

import com.tablelog.tablelogback.domain.follower.entity.Follow;
import com.tablelog.tablelogback.domain.follower.exception.AlreadyExistsFollowingException;
import com.tablelog.tablelogback.domain.follower.exception.FollowErrorCode;
import com.tablelog.tablelogback.domain.follower.exception.SelfFollowNotAllowedException;
import com.tablelog.tablelogback.domain.follower.repository.FollowRepository;
import com.tablelog.tablelogback.domain.follower.service.FollowService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public void createFollow(Long followingId, User user) {
        if(user.getId().equals(followingId)){
            throw new SelfFollowNotAllowedException(FollowErrorCode.CAN_NOT_FOLLOW_SELF);
        }
        User following = findUser(followingId);
        if(followRepository.existsByFollowerIdAndFollowingId(user.getId(), following.getId())){
            throw new AlreadyExistsFollowingException(FollowErrorCode.ALREADY_EXIST_FOLLOWING);
        }
        Follow follow = Follow.builder()
                .followerId(user.getId())
                .followingId(following.getId())
                .build();
        followRepository.save(follow);
        user.updateFollowingCount(user.getFollowingCount() + 1);
        following.updateFollowerCount(following.getFollowerCount() + 1);
        userRepository.save(user);
        userRepository.save(following);
    }

    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
    }
}
