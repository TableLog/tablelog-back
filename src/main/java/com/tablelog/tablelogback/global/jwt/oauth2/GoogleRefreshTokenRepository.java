package com.tablelog.tablelogback.global.jwt.oauth2;

import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GoogleRefreshTokenRepository extends CrudRepository<GoogleRefreshToken, String> {
    Optional<GoogleRefreshToken> findByGoogleRefreshToken(String refreshToken);
}
