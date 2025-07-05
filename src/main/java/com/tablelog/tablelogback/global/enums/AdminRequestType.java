package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AdminRequestType {
    WITHDRAWAL,    // 회원탈퇴 요청
    EXPERT_VERIFY;  // 전문가 인증 요청

    @JsonCreator
    public static AdminRequestType fromString(String inputValue) {
        return EnumUtils.fromString(AdminRequestType.class, inputValue);
    }
}
