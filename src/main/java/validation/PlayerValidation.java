package validation;

import exception.ValidationException;
import model.Player;
import util.PlayerUtils;

public class PlayerValidation {
    public static void validatePlayerForCreate(Player player) {
        if (player == null) {
            throw new ValidationException("Player cannot be null");
        }

        validatePlayerName(player.name());
    }

    public static void validatePlayerForRead(Player player) {
        if (player == null) {
            throw new ValidationException("Player cannot be null");
        }

        validatePlayerId(player.id());
        validatePlayerName(player.name());
    }

    public static void validatePlayerNames(String name1, String name2) {
        validatePlayerName(name1);
        validatePlayerName(name2);

        if (PlayerUtils.normalizeInput(name1).equals(PlayerUtils.normalizeInput(name2))) {
            throw new ValidationException("Player names should be unique and cannot be equal");
        }
    }

    public static void validatePlayerId(Integer id) {
        if (id == null) {
            throw new ValidationException("player id cannot be null");
        }

        if (id < 0) {
            throw new ValidationException("player id must be non-negative");
        }
    }

    public static void validatePlayerName(String name) {
        if (name == null) {
            throw new ValidationException("playerName cannot be null");
        }

        if (name.trim().isEmpty()) {
            throw new ValidationException("playerName cannot be empty or blank");
        }

        String normalizedName = PlayerUtils.normalizeInput(name);
        if (normalizedName.length() < 2 || normalizedName.length() > 20) {
            throw new ValidationException("playerName length must be between 2 and 20 characters");
        }

        if (!normalizedName.matches("^[a-z][a-z '-]{1,19}$")) {
            throw new ValidationException(
                    "playerName must contain only ASCII symbols"
            );
        }
    }
}
