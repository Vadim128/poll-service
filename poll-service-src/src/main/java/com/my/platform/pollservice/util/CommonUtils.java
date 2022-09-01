package com.my.platform.pollservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    public long toPrimitive(Long value) {
        return value == null ? 0 : value;
    }
}
