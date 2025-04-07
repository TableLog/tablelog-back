package com.tablelog.tablelogback.global.jwt;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
@Getter
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RedisHash(value = "RefreshToken")
public class RefreshToken {
    @Id
    private final Long id;

    @Indexed
    private final String refreshToken;

    private final Long timeToLive;

    @Builder
    public RefreshToken(Long id, String refreshToken, Long timeToLive){
        this.id = id;
        this.refreshToken = refreshToken;
        this.timeToLive = timeToLive;
    }
}
