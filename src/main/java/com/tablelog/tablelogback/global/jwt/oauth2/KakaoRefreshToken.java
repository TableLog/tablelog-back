package com.tablelog.tablelogback.global.jwt.oauth2;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Getter
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RedisHash("KakaoRefreshToken")
public class KakaoRefreshToken {

    @Id
    private final Long id;

    @Indexed
    private final String kakaoRefreshToken;

    private final Integer timeToLive;

    @Builder
    public KakaoRefreshToken(Long id, String kakaoRefreshToken, Integer timeToLive){
        this.id = id;
        this.kakaoRefreshToken = kakaoRefreshToken;
        this.timeToLive = timeToLive;
    }
}
