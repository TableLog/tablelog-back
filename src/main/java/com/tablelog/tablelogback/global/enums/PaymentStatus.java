package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentStatus {
    결제중,
    대기중,
    결제완료,
    결제취소,
    결제실패;
    @JsonCreator
    public static PaymentStatus fromString(String inputValue) {
        return EnumUtils.fromString(PaymentStatus.class, inputValue);
    }
}
