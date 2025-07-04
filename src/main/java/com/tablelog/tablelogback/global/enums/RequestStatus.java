package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequestStatus {
    REQUESTED,
    COMPLETED,
    DECLINED,
    APPROVE;

    @JsonCreator
    public static RequestStatus fromString(String inputValue) {
        return EnumUtils.fromString(RequestStatus.class, inputValue);
    }
}
