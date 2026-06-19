package service;

import exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.MatchState;
import validation.MatchValidation;

@Slf4j
public class MatchScoreCalculationService {
    public MatchState calculate(MatchState state, Integer pointWinnerPlayerId) {
        validatePointInput(state, pointWinnerPlayerId);
        MatchValidation.validateMatchState(state);
        state.awardPointTo(pointWinnerPlayerId);
        MatchValidation.validateMatchState(state);
        return state;
    }

    private void validatePointInput(MatchState state, Integer pointWinnerPlayerId) {
        if (state == null) {
            throw new ValidationException("Match state must not be null.");
        }
        if (state.getPlayer1Id() == null || state.getPlayer2Id() == null) {
            throw new ValidationException("Player id must not be null.");
        }
        if (pointWinnerPlayerId == null) {
            throw new ValidationException("Point winner id must not be null.");
        }
        if (!pointWinnerPlayerId.equals(state.getPlayer1Id())
                && !pointWinnerPlayerId.equals(state.getPlayer2Id())) {
            throw new ValidationException("Point winner is not part of this match.");
        }
        if (state.isFinished()) {
            throw new ValidationException("Match is already finished.");
        }
    }
}
