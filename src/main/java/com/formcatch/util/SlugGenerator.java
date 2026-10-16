package com.formcatch.util;

import java.security.SecureRandom;

/**
 * 短链接 slug 生成器
 */
public final class SlugGenerator {

    private static final char[] ALPHABET = "abcdefghijkmnpqrstuvwxyz23456789".toCharArray(); // 去掉易混字符
    private static final SecureRandom RANDOM = new SecureRandom();

    private SlugGenerator() {}

    /**
     * 生成指定长度的随机 slug
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    public static String generate() {
        return generate(8);
    }
}
