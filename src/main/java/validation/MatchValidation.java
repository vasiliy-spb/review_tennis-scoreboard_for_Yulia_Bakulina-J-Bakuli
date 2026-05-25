package validation;

import exception.ValidationException;
import util.PlayerUtils;

public class MatchValidation {
    public static void validateMatchUuid(String uuid) {
        if (uuid == null) {
            throw new ValidationException("uuid cannot be null");
        }

        if (uuid.trim().isEmpty()) {
            throw new ValidationException("uuid cannot be empty or blank");
        }
    }

    public static void validateWinner(String winner) {
        if (winner == null) {
            throw new ValidationException("winner cannot be null");
        }

        if (winner.trim().isEmpty()) {
            throw new ValidationException("winner cannot be empty or blank");
        }

        String normalizedWinner = PlayerUtils.normalizeInput(winner);

        if (!normalizedWinner.equals("player1") && !normalizedWinner.equals("player2")) {
            throw new ValidationException("winner must be either player1 or player2");
        }
    }
}
