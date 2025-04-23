package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserProvider {
    local,
    kakao,
    google;

    @JsonCreator
    public static UserProvider fromString(String inputValue) {
        return EnumUtils.fromString(UserProvider.class, inputValue);
    }
}
