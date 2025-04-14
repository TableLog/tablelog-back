package com.tablelog.tablelogback.global.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    NORMAL(Authority.NORMAL),
    EXPERT(Authority.EXPERT),
    ADMIN(Authority.ADMIN);

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {

//        public static final String USER = "ROLE_USER";
        public static final String NORMAL = "ROLE_NORMAL";
        public static final String EXPERT = "ROLE_EXPERT";
        public static final String ADMIN = "ROLE_ADMIN";
    }

}