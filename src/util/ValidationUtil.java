package src.util;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isPositiveInt(String s) {
        try {
            return Integer.parseInt(s) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String s) {
        try {
            return Double.parseDouble(s) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
