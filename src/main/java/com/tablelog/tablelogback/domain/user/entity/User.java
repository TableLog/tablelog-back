package com.tablelog.tablelogback.domain.user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String userName;

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

    @Column(nullable = false)
    private Boolean marketingOptIn;

    @Transient
    private List<OAuthAccount> oAuthAccounts;

    @Column(nullable = false)
    private Long recipeCount;

    @Column(nullable = false)
    private Long boardCount;

    @Column(nullable = false)
    private Long followerCount;;

    @Column(nullable = false)
    private Long followingCount;;

    @Builder
    public User(
            final String email,
            final String password,
            final String nickname,
            final String userName,
            final String birthday,
            final UserRole userRole,
            final String profileImgUrl,
            final String folderName,
            final UserProvider provider,
            final Boolean marketingOptIn
    ){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userName = userName;
        this.birthday = birthday;
        this.userRole = UserRole.NORMAL;
        this.profileImgUrl = profileImgUrl;
        this.folderName = folderName;
        this.provider = provider;
        this.pointBalance = 0;
        this.marketingOptIn = marketingOptIn;
        this.recipeCount = 0L;
        this.boardCount = 0L;
        this.followerCount = 0L;
        this.followingCount = 0L;
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

    public void updateMarketingOptIn(Boolean marketingOptIn){
        this.marketingOptIn = marketingOptIn;
    }

    public void updateRecipeCount(Long recipeCount){
        this.recipeCount = recipeCount;
    }

    public void updateBoardCount(Long boardCount){
        this.boardCount = boardCount;
    }

    public void updateFollowerCount(Long followerCount){
        this.followerCount = followerCount;
    }

    public void updateFollowingCount(Long followingCount){
        this.followingCount = followingCount;
    }

    public void setOauthAccounts(List<OAuthAccount> accounts) {
        this.oAuthAccounts = accounts;
    }
}
