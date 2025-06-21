package com.tablelog.tablelogback.global.jwt;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash(value = "RefreshToken")
public class RefreshToken {
    @Id
    private Long id;

    @Indexed
    private String refreshToken;

    private Long timeToLive;
}