package validation;

import exception.ValidationException;
import util.PlayerUtils;

public class PlayerNameValidation {
    public static void validatePlayerNames(String name1, String name2) {
        validatePlayerName(name1);
        validatePlayerName(name2);

        if (PlayerUtils.normalizeName(name1).equals(PlayerUtils.normalizeName(name2))) {
            throw new ValidationException("Player names should be unique and cannot be equal");
        }
    }

    public static void validatePlayerName(String name) {
        if (name == null) {
            throw new ValidationException("playerName cannot be null");
        }

        if (name.trim().isEmpty()) {
            throw new ValidationException("playerName cannot be empty or blank");
        }

        String normalizedName = PlayerUtils.normalizeName(name);
        if (normalizedName.length() < 2 || normalizedName.length() > 20) {
            throw new ValidationException("playerName length must be between 2 and 20 characters");
        }

        if (!normalizedName.matches("^[A-Za-z][A-Za-z\\s'-]{1,19}$")) {
            throw new ValidationException(
                    "playerName must contain only Latin letters, spaces, apostrophes or hyphens"
            );
        }
    }
}
