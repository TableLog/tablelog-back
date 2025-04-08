package com.tablelog.tablelogback.global.jwt.oauth2;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Getter
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RedisHash("GoogleRefreshToken")
public class GoogleRefreshToken {

    @Id
    private final Long id;

    @Indexed
    private final String googleRefreshToken;

    private final Long timeToLive;

    @Builder
    public GoogleRefreshToken(Long id, String googleRefreshToken, Long timeToLive){
        this.id = id;
        this.googleRefreshToken = googleRefreshToken;
        this.timeToLive = timeToLive;
    }
}
