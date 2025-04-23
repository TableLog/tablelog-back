package com.tablelog.tablelogback.domain.user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_USER")
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String birthday;

    @Column
    private String profileImgUrl;

    @Column
    private String folderName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserProvider provider;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column
    private Integer pointBalance;

    @Builder
    public User(
            final String email,
            final String password,
            final String nickname,
            final String name,
            final String birthday,
            final UserRole userRole,
            final String profileImgUrl,
            final String folderName,
            final UserProvider provider,
            final Integer pointBalance
    ){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birthday = birthday;
        this.userRole = UserRole.NORMAL;
        this.profileImgUrl = profileImgUrl;
        this.folderName = folderName;
        this.provider = provider;
        this.pointBalance = 0;
    }

    public void changeRole(UserRole newUserRole) {
        this.userRole = newUserRole;
    }

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updatePointBalance(Integer point){
        this.pointBalance = this.pointBalance + point;
    }

    public void updateProvider(UserProvider provider){
        this.provider = provider;
    }
}
