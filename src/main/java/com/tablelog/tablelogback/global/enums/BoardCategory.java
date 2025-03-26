package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BoardCategory {
    게시판,
    공지사항,
    QNA,
    FAQ;
    @JsonCreator
    public static BoardCategory fromString(String inputValue) {
        return EnumUtils.fromString(BoardCategory.class, inputValue);
    }
}
