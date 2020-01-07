package com.github.linkeer8802.octopus.core.util;

import java.util.UUID;

/**
 * ID标识符工具类
 * @author weird
 */
public final class Identifiers {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
