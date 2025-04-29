package com.tablelog.tablelogback.domain.user.repository;

import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickName);

    Optional<User> findByUserNameAndBirthday(String userName, String birthday);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByUserNameAndBirthday(String userName, String birthday);
}
