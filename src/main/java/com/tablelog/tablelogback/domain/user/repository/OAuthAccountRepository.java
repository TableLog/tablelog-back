package com.tablelog.tablelogback.domain.user.repository;

import com.tablelog.tablelogback.domain.user.entity.OAuthAccount;
import com.tablelog.tablelogback.global.enums.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<List<OAuthAccount>> findAllByUserId(Long userId);
    OAuthAccount findByProviderAndEmail(UserProvider provider, String email);
    void deleteAllByUserId(Long userId);
    Boolean existsByEmail(String email);
    Boolean existsByProviderAndEmail(UserProvider provider, String email);
}
