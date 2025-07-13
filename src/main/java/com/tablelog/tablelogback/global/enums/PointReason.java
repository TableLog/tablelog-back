package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PointReason {
    회원가입,
    레시피등록,
    피드등록,
    레시피댓글등록,
    레시피구매,
    레시피판매;

    @JsonCreator
    public static PointReason fromString(String inputValue) {
        return EnumUtils.fromString(PointReason.class, inputValue);
    }
}
