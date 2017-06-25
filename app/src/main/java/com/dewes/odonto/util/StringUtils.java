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

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isUsernameValid(String username) {
        return !username.contains(" ") && !hasSpecial(username) && !hasUpperCase(username);
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 3 && !hasSpecial(password);
    }

    public static boolean isLoginValid(String login) {
        return !login.contains(" ") && !hasUpperCase(login);
    }

}
