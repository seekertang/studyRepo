package com.ethansolutions.morpheus.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtil {
    public static byte[] getBytes(String string, Charset charset) {
        return string == null ? new byte[0] : string.getBytes(charset);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
