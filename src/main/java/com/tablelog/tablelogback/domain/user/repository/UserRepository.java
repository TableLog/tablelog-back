package com.tablelog.tablelogback.domain.user.repository;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUserNameDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUserNicknameDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickName);

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameAndBirthday(String userName, String birthday);

    @Query("SELECT new com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUserNicknameDto(u.id, u.nickname) " +
            "FROM User u WHERE u.id IN :userIds")
    List<RecipeUserNicknameDto> findNicknamesByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT new com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUserNameDto(u.id, u.userName) " +
            "FROM User u WHERE u.id IN :userIds")
    List<RecipeUserNameDto> findUserNamesByUserIds(@Param("userIds") List<Long> userIds);

    Slice<User> findByNicknameContaining(String keyword, Pageable pageable);

    List<User> findByUserRoleAndDeletedAtBefore(UserRole userRole, LocalDateTime now);

    List<User> findAllByNicknameIn(Set<String> nicknames);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByUserNameAndBirthday(String userName, String birthday);

    @Query(value = """
    SELECT DATE(created_at) AS date, COUNT(*) AS count
      FROM tb_user
     WHERE created_at >= :startDate
     GROUP BY DATE(created_at)
     ORDER BY DATE(created_at) DESC
    """, nativeQuery = true)
    List<Object[]> findDailySignUpCount(@Param("startDate") LocalDateTime startDate);

    @Query("""
    SELECT u
     FROM User u
    WHERE u.nickname LIKE CONCAT('%', :keyword, '%')
       OR u.email LIKE CONCAT('%', :keyword, '%')
       OR u.userName LIKE CONCAT('%', :keyword, '%')
""")
    Slice<User> searchUsersByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
