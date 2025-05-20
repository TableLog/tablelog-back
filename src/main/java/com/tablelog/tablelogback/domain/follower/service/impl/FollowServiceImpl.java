package com.tablelog.tablelogback.domain.follower.service.impl;

import com.tablelog.tablelogback.domain.follower.dto.FollowUserDto;
import com.tablelog.tablelogback.domain.follower.dto.FollowUserListDto;
import com.tablelog.tablelogback.domain.follower.entity.Follow;
import com.tablelog.tablelogback.domain.follower.exception.AlreadyExistsFollowingException;
import com.tablelog.tablelogback.domain.follower.exception.FollowErrorCode;
import com.tablelog.tablelogback.domain.follower.exception.NotFoundFollowingException;
import com.tablelog.tablelogback.domain.follower.exception.SelfFollowNotAllowedException;
import com.tablelog.tablelogback.domain.follower.repository.FollowRepository;
import com.tablelog.tablelogback.domain.follower.service.FollowService;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

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

    @Override
    public void deleteFollow(Long followingId, User user) {
        User following = findUser(followingId);
        Follow follow = findFollow(followingId, user.getId());
        followRepository.delete(follow);
        user.updateFollowingCount(user.getFollowingCount() - 1);
        following.updateFollowerCount(following.getFollowerCount() - 1);
        userRepository.save(user);
        userRepository.save(following);
    }

    @Override
    public Boolean isFollowing(Long followingId, User user) {
        User following = findUser(followingId);
        findFollow(followingId, user.getId());
        return followRepository.existsByFollowerIdAndFollowingId(user.getId(), followingId);
    }

    @Transactional
    public Long getFollowerCountByUser(Long userId) {
        User user = findUser(userId);
        Long c = followRepository.countFollowerIdByFollowingId(userId);
        user.updateFollowerCount(c);
        return c;
    }

    @Transactional
    public Long getFollowingCountByUser(Long userId) {
        User user = findUser(userId);
        Long c = followRepository.countFollowingIdByFollowerId(userId);
        user.updateFollowingCount(c);
        return c;
    }

    @Override
    public FollowUserListDto getFollowers(Long userId, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<User> slice = followRepository.findAllFollowersByFollowingId(userId, pageRequest);
        List<User> users = slice.getContent();

        // 로그인 유저(me)가 팔로우하고 있는 Id 중 교집합
        List<Long> targetIds = users.stream()
                .map(User::getId)
                .toList();

        Set<Long> idsIsFollow = new HashSet<>(
                followRepository.findAllFollowingIdsByFollowerId(userId, targetIds)
        );
        List<FollowUserDto> dtos = users.stream()
                .map(user -> new FollowUserDto(
                        user.getId(),
                        user.getNickname(),
                        idsIsFollow.contains(user.getId())
                ))
                .toList();
        return new FollowUserListDto(dtos, slice.hasNext());
    }

    @Override
    public FollowUserListDto getFollowings(Long userId, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<User> slice = followRepository.findAllFollowingsByFollowerId(userId, pageRequest);
        List<User> users = slice.getContent();
        List<Long> targetIds = users.stream()
                .map(User::getId)
                .toList();
        Set<Long> idsIsFollow = new HashSet<>(
                followRepository.findAllFollowingIdsByFollowerId(userId, targetIds)
        );
        List<FollowUserDto> dtos = users.stream()
                .map(user -> new FollowUserDto(
                        user.getId(),
                        user.getNickname(),
                        idsIsFollow.contains(user.getId())
                ))
                .toList();
        return new FollowUserListDto(dtos, slice.hasNext());
    }

    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
    }

    private Follow findFollow(Long followingId, Long userId){
        return followRepository.findByFollowerIdAndFollowingId(followingId, userId)
                .orElseThrow(() -> new NotFoundFollowingException(FollowErrorCode.NOT_FOUND_FOLLOWING));
    }
}
