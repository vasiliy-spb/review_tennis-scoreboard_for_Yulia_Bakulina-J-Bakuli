package util;

import java.util.Locale;

public final class PlayerUtils {
    private PlayerUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String normalizeInput(String input) {
        return input.trim().toLowerCase(Locale.ROOT);
    }
}
