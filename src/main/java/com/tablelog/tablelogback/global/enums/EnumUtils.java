package com.tablelog.tablelogback.global.enums;

import java.util.stream.Stream;

public class EnumUtils {
    // 예외처리 쉽게 하기 위해서 해당 Enum 클래스에 정의되지 않은 문자열이 들어올 경우 null로 바꾼다.
    public static <T extends Enum<T>> T fromString(Class<T> enumClass, String inputValue) {
        if (inputValue == null) {
            return null;
        }
        return Stream.of(enumClass.getEnumConstants())
            .filter(e -> e.toString().equalsIgnoreCase(inputValue))
            .findFirst()
            .orElse(null);
    }
}

