package com.tablelog.tablelogback.global.jwt.oauth2;

import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KakaoRefreshTokenRepository extends CrudRepository<KakaoRefreshToken, String> {
    Optional<KakaoRefreshToken> findByKakaoRefreshToken(String refreshToken);
}
