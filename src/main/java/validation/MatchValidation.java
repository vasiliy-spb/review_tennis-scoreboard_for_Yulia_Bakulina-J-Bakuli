package validation;

import exception.ValidationException;
import model.MatchState;
import model.OngoingMatch;
import util.PlayerUtils;

import java.util.Objects;
import java.util.UUID;

public class MatchValidation {
    public static void validateMatchUuid(String uuid) {
        if (uuid == null) {
            throw new ValidationException("uuid cannot be null");
        }

        if (uuid.trim().isEmpty()) {
            throw new ValidationException("uuid cannot be empty or blank");
        }
    }

    public static UUID parseUuid(String uuid) {
        UUID parsed;
        try {
            parsed = UUID.fromString(uuid.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("uuid has an invalid format");
        }
        return parsed;
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

    public static void validateOngoingMatch(OngoingMatch ongoingMatch) {
        if (ongoingMatch == null) {
            throw new ValidationException("ongoingMatch cannot be null");
        }

        UUID uuid = ongoingMatch.getUuid();
        Integer player1Id = ongoingMatch.getPlayer1();
        Integer player2Id = ongoingMatch.getPlayer2();
        MatchState matchState = ongoingMatch.getMatchState();
        Integer winnerId = matchState.getWinnerPlayerId();

        validateNotNullId(uuid, player1Id, player2Id);
        validateMatchState(matchState);
        validatePlayersConsistency(player1Id, player2Id, matchState);
        validateWinnerConsistency(winnerId, player1Id, player2Id, matchState);
    }

    public static void validateMatchState(MatchState matchState) {
        if (matchState == null) {
            throw new ValidationException("matchState cannot be null");
        }
        validateNonNegativeScores(matchState);
    }

    private static void validateNotNullId(UUID uuid, Integer player1Id, Integer player2Id) {
        if (uuid == null) {
            throw new ValidationException("ongoingMatch uuid cannot be null");
        }

        if (player1Id == null) {
            throw new ValidationException("player1Id cannot be null");
        }

        if (player2Id == null) {
            throw new ValidationException("player2Id cannot be null");
        }
    }

    private static void validateNonNegativeScores(MatchState matchState) {
        if (matchState.getPlayer1GamesInSet() < 0) {
            throw new ValidationException("player1GamesInSet cannot be negative");
        }

        if (matchState.getPlayer2GamesInSet() < 0) {
            throw new ValidationException("player2GamesInSet cannot be negative");
        }

        if (matchState.getPlayer1TieBreakPoints() < 0) {
            throw new ValidationException("player1TieBreakPoints cannot be negative");
        }

        if (matchState.getPlayer2TieBreakPoints() < 0) {
            throw new ValidationException("player2TieBreakPoints cannot be negative");
        }
    }

    private static void validatePlayersConsistency(Integer player1, Integer player2, MatchState matchState) {
        if (player1 == null) {
            throw new ValidationException("player1 cannot be null");
        }

        if (player2 == null) {
            throw new ValidationException("player2 cannot be null");
        }

        if (player1.equals(player2)) {
            throw new ValidationException("players 1 and 2 must be different");
        }

        if (!Objects.equals(player1, matchState.getPlayer1Id())) {
            throw new ValidationException("player1 from ongoingMatch must be the same as the one from matchState");
        }

        if (!Objects.equals(player2, matchState.getPlayer2Id())) {
            throw new ValidationException("player2 from ongoingMatch must be the same as the one from matchState");
        }
    }

    private static void validateWinnerConsistency(Integer winner, Integer player1, Integer player2, MatchState matchState) {
        if (winner != null && !Objects.equals(winner, player1) && !Objects.equals(winner, player2)) {
            throw new ValidationException("winner must be one of ongoing match players");
        }
    }
}
