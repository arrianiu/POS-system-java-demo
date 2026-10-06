package src.util;

import java.time.LocalDateTime;

public class UserSession {

    private static String username;
    private static String role;

    private UserSession() {
        // prevent instantiation
    }

    // ===== START SESSION =====
    private static LocalDateTime loginTime;

    public static void startSession(String username, String role) {
        UserSession.username = username;
        UserSession.role = role;
        loginTime = LocalDateTime.now(); // ⭐ THIS IS THE KEY
    }

    public static LocalDateTime getLoginTime() {
        return loginTime;
    }

    // ===== GETTERS =====
    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    // ===== ROLE HELPERS =====
    public static boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    public static boolean isCashier() {
        return role != null && role.equalsIgnoreCase("cashier");
    }

    // ===== SESSION STATUS =====
    public static boolean isLoggedIn() {
        return username != null && role != null;
    }

    // ===== LOGOUT (THIS WAS MISSING) =====
    public static void logout() {
        username = null;
        role = null;
    }

}
