package util;

import java.util.Locale;

public final class MatchesQueryUtils {
    private MatchesQueryUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String normalizeFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }
        return filter.trim().toLowerCase(Locale.ROOT);
    }
}
