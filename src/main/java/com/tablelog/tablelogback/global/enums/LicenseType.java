package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LicenseType {
    사업자등록증,
    특허증,
    개수;

    @JsonCreator
    public static LicenseType fromString(String inputValue) {
        return EnumUtils.fromString(LicenseType.class, inputValue);
    }
}
