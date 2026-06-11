package validation;

import exception.ValidationException;

public class MatchesQueryValidation {
    public static int parsePage(String page) {
        if (page == null || page.isBlank()) {
            return 1;
        }

        try {
            return Math.max(Integer.parseInt(page.trim()), 1);
        } catch (NumberFormatException e) {
            throw new ValidationException("Page must be a positive integer");
        }
    }
}
