package com.dewes.odonto.util;

/**
 * Created by Dewes on 18/06/2017.
 */

public class StringUtils {

    public static boolean hasSpecial(String txt) {
        return !txt.matches("[A-Za-z0-9 ]*");
    }

    public static boolean hasUpperCase(String txt) {
        return !txt.equals(txt.toLowerCase());
    }
}
