package service;

import exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.MatchScoreResult;
import model.MatchState;
import validation.MatchValidation;

@Slf4j
public class MatchScoreCalculationService {
    public MatchScoreResult calculate(MatchState state, Integer pointWinnerPlayerId) {
        validatePointInput(state, pointWinnerPlayerId);
        MatchValidation.validateMatchState(state);
        applyPoint(state, pointWinnerPlayerId);
        processGameTransition(state, pointWinnerPlayerId);
        processSetTransition(state);
        processMatchFinish(state);
        MatchValidation.validateMatchState(state);
        return buildResult(state);
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

    private void applyPoint(MatchState state, Integer pointWinnerPlayerId) {
        if (state.isTieBreak()) {
            applyTieBreakPoint(state, pointWinnerPlayerId);
        } else {
            applyRegularGamePoint(state, pointWinnerPlayerId);
        }
    }

    private void applyTieBreakPoint(MatchState state, Integer pointWinnerPlayerId) {
        if (pointWinnerPlayerId.equals(state.getPlayer1Id())) {
            state.setPlayer1TieBreakPoints(state.getPlayer1TieBreakPoints() + 1);
        } else if (pointWinnerPlayerId.equals(state.getPlayer2Id())) {
            state.setPlayer2TieBreakPoints(state.getPlayer2TieBreakPoints() + 1);
        } else {
            throw new ValidationException("Point winner is not part of this match.");
        }
    }

    private void applyRegularGamePoint(MatchState state, Integer pointWinnerPlayerId) {
        if (pointWinnerPlayerId.equals(state.getPlayer1Id())) {
            state.setPlayer1PointsInGame(state.getPlayer1PointsInGame() + 1);
        } else if (pointWinnerPlayerId.equals(state.getPlayer2Id())) {
            state.setPlayer2PointsInGame(state.getPlayer2PointsInGame() + 1);
        } else {
            throw new ValidationException("Point winner is not part of this match.");
        }
    }


    private void processGameTransition(MatchState state, Integer pointWinnerPlayerId) {
        if (state.isTieBreak()) {
            if (isTieBreakFinished(state)) {
                awardGameToPointWinner(state, pointWinnerPlayerId);
            }
            return;
        }

        if (isRegularGameFinished(state)) {
            awardGameToPointWinner(state, pointWinnerPlayerId);
            resetRegularGamePoints(state);
        }
    }

    private boolean isRegularGameFinished(MatchState state) {
        int p1 = state.getPlayer1PointsInGame();
        int p2 = state.getPlayer2PointsInGame();
        return (p1 >= 4 || p2 >= 4) && Math.abs(p1 - p2) >= 2;
    }

    private boolean isTieBreakFinished(MatchState state) {
        int p1 = state.getPlayer1TieBreakPoints();
        int p2 = state.getPlayer2TieBreakPoints();
        return (p1 >= 7 || p2 >= 7) && Math.abs(p1 - p2) >= 2;
    }

    private void awardGameToPointWinner(MatchState state, Integer pointWinnerPlayerId) {
        if (pointWinnerPlayerId.equals(state.getPlayer1Id())) {
            state.setPlayer1GamesInSet(state.getPlayer1GamesInSet() + 1);
        } else if (pointWinnerPlayerId.equals(state.getPlayer2Id())) {
            state.setPlayer2GamesInSet(state.getPlayer2GamesInSet() + 1);
        } else {
            throw new ValidationException("Point winner is not part of this match.");
        }
    }

    private void resetRegularGamePoints(MatchState state) {
        state.setPlayer1PointsInGame(0);
        state.setPlayer2PointsInGame(0);
    }

    private void processSetTransition(MatchState state) {
        if (shouldStartTieBreak(state)) {
            state.setTieBreak(true);
        }
        if (isSetFinished(state)) {
            awardSetToSetWinner(state);
            resetSetState(state);
        }
    }

    private boolean isSetFinished(MatchState state) {
        int p1Games = state.getPlayer1GamesInSet();
        int p2Games = state.getPlayer2GamesInSet();
        if ((p1Games >= 6 || p2Games >= 6) && Math.abs(p1Games - p2Games) >= 2) {
            return true;
        }
        return (p1Games == 7 && p2Games == 6) || (p1Games == 6 && p2Games == 7);
    }

    private boolean shouldStartTieBreak(MatchState state) {
        return state.getPlayer1GamesInSet() == 6
                && state.getPlayer2GamesInSet() == 6
                && !state.isTieBreak();
    }

    private void processMatchFinish(MatchState state) {
        if (state.getPlayer1Sets() == 2) {
            state.setFinished(true);
            state.setWinnerPlayerId(state.getPlayer1Id());
        } else if (state.getPlayer2Sets() == 2) {
            state.setFinished(true);
            state.setWinnerPlayerId(state.getPlayer2Id());
        }
    }

    private void awardSetToSetWinner(MatchState state) {
        int p1Games = state.getPlayer1GamesInSet();
        int p2Games = state.getPlayer2GamesInSet();
        if (p1Games > p2Games) {
            state.setPlayer1Sets(state.getPlayer1Sets() + 1);
        } else if (p2Games > p1Games) {
            state.setPlayer2Sets(state.getPlayer2Sets() + 1);
        } else {
            throw new ValidationException("Cannot award set when games are equal.");
        }
    }

    private void resetSetState(MatchState state) {
        state.setPlayer1GamesInSet(0);
        state.setPlayer2GamesInSet(0);
        state.setPlayer1PointsInGame(0);
        state.setPlayer2PointsInGame(0);
        state.setTieBreak(false);
        state.setPlayer1TieBreakPoints(0);
        state.setPlayer2TieBreakPoints(0);
    }

    private MatchScoreResult buildResult(MatchState state) {
        return new MatchScoreResult(state, state.isFinished(), state.getWinnerPlayerId());
    }
}
