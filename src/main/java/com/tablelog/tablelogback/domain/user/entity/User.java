package com.tablelog.tablelogback.domain.user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
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

    @Column
    private String profileImgUrl;

    @Column
    private String folderName;

    @Column
    private String kakaoEmail;

    @Column
    private String googleEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder
    public User(
            final String email,
            final String password,
            final String nickname,
            final UserRole userRole,
            final String profileImgUrl,
            final String folderName,
            final String kakaoEmail,
            final String googleEmail
    ){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = UserRole.USER;
        this.profileImgUrl = profileImgUrl;
        this.folderName = folderName;
        this.kakaoEmail = kakaoEmail;
        this.googleEmail = googleEmail;
    }

    public void changeRole(UserRole newUserRole) {
        this.userRole = newUserRole;
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

    public void updateKakaoEmail(String kakaoEmail){
        this.kakaoEmail = kakaoEmail;
    }

    public void updateGoogleEmail(String googleEmail){
        this.googleEmail = googleEmail;
    }

    public void deleteKakaoEmail(){
        this.kakaoEmail = null;
    }

    public void deleteGoogleEmail(){
        this.googleEmail = null;
    }
}
