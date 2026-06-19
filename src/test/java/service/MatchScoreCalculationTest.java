package service;

import exception.ValidationException;
import model.GamePoint;
import model.MatchState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchScoreCalculationTest {
    private MatchScoreCalculationService service;
    private MatchState state;

    @BeforeEach
    public void setUp() {
        service = new MatchScoreCalculationService();
        state = basicMatchState();
    }

    @Test
    public void validateNullStateTest() {
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(null, 1));
    }

    @Test
    public void validateInvalidWinnerTest() {
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, 99));
    }

    @Test
    public void validateNullPlayerIdsTest() {
        Assertions.assertThrows(ValidationException.class,
                () -> service.calculate(new MatchState(null, 2), 1));
        Assertions.assertThrows(ValidationException.class,
                () -> service.calculate(new MatchState(1, null), 1));
    }

    @Test
    public void validateNullPointWinnerPlayerIdTest() {
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, null));
    }

    @Test
    public void validateFinishedStateTest() {
        winSetSixToZero(state, 1);
        winSetSixToZero(state, 1);
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, 1));
    }

    @Test
    public void shouldIncreasePlayer2PointByOneWhenPlayer2WinsTest() {
        service.calculate(state, 2);
        Assertions.assertEquals(GamePoint.LOVE, state.getPlayer1PointsInGame());
        Assertions.assertEquals(GamePoint.FIFTEEN, state.getPlayer2PointsInGame());
    }

    @Test
    public void shouldAwardGameAndResetGamePointsWhenPlayerWinsFromThreePointsTest() {
        awardPoints(state, 1, 3);
        service.calculate(state, 1);
        Assertions.assertEquals(1, state.getPlayer1GamesInSet());
        Assertions.assertEquals(GamePoint.LOVE, state.getPlayer1PointsInGame());
        Assertions.assertEquals(0, state.getPlayer2GamesInSet());
        Assertions.assertEquals(GamePoint.LOVE, state.getPlayer2PointsInGame());
    }

    @Test
    public void advantageLessThan2ShouldNotFinishGameTest() {
        awardPoints(state, 1, 3);
        awardPoints(state, 2, 3);

        service.calculate(state, 1);

        Assertions.assertEquals(GamePoint.ADVANTAGE, state.getPlayer1PointsInGame());
        Assertions.assertEquals(GamePoint.FORTY, state.getPlayer2PointsInGame());
        Assertions.assertEquals(0, state.getPlayer1GamesInSet());
        Assertions.assertEquals(0, state.getPlayer2GamesInSet());
    }

    @Test
    public void shouldAwardGameWhenLeadBecomesTwoAfterAdvantageTest() {
        awardPoints(state, 1, 3);
        awardPoints(state, 2, 3);
        state.awardPointTo(1);

        service.calculate(state, 1);

        Assertions.assertEquals(GamePoint.LOVE, state.getPlayer1PointsInGame());
        Assertions.assertEquals(GamePoint.LOVE, state.getPlayer2PointsInGame());
        Assertions.assertEquals(1, state.getPlayer1GamesInSet());
        Assertions.assertEquals(0, state.getPlayer2GamesInSet());
    }

    @Test
    public void sixSixShouldStartTieBreakTest() {
        reachGamesSixSix(state);
        service.calculate(state, 1);
        Assertions.assertTrue(state.isTieBreak());
    }

    @Test
    public void tieBreakWinEightToSixShouldAwardSetTest() {
        startTieBreakAtGamesSixSix(state);
        awardPoints(state, 1, 6);
        awardPoints(state, 2, 6);

        MatchState resultAfterSevenSix = service.calculate(state, 1);
        Assertions.assertEquals(0, resultAfterSevenSix.getPlayer1Sets());
        Assertions.assertEquals(0, resultAfterSevenSix.getPlayer2Sets());

        MatchState resultAfterEightSix = service.calculate(state, 1);
        Assertions.assertEquals(1, resultAfterEightSix.getPlayer1Sets());
        Assertions.assertEquals(0, resultAfterEightSix.getPlayer2Sets());

        Assertions.assertFalse(resultAfterEightSix.isTieBreak());
    }

    @Test
    public void tieBreakShouldNotFinishWithoutTwoPointsDifferenceTest() {
        startTieBreakAtGamesSixSix(state);
        awardPoints(state, 1, 6);
        awardPoints(state, 2, 6);

        service.calculate(state, 1);

        Assertions.assertTrue(state.isTieBreak());
        Assertions.assertEquals(6, state.getPlayer1GamesInSet());
        Assertions.assertEquals(6, state.getPlayer2GamesInSet());
    }

    @Test
    public void shouldFinishMatchAtTwoSetsToZeroTest() {
        winSetSixToZero(state, 1);
        for (int i = 0; i < 5; i++) {
            winGame(state, 1);
        }
        awardPoints(state, 1, 3);

        service.calculate(state, 1);
        Assertions.assertTrue(state.isFinished());
        Assertions.assertEquals(1, state.getWinnerPlayerId());
    }

    @Test
    public void shouldFinishMatchAtTwoSetsToOneTest() {
        winSetSixToZero(state, 1);
        winSetSixToZero(state, 2);
        for (int i = 0; i < 5; i++) {
            winGame(state, 2);
        }
        awardPoints(state, 2, 3);

        service.calculate(state, 2);

        Assertions.assertTrue(state.isFinished());
        Assertions.assertEquals(2, state.getWinnerPlayerId());
        Assertions.assertEquals(2, state.getPlayer2Sets());
        Assertions.assertEquals(1, state.getPlayer1Sets());
    }

    private MatchState basicMatchState() {
        return new MatchState(1, 2);
    }

    private void awardPoints(MatchState state, int playerId, int count) {
        for (int i = 0; i < count; i++) {
            state.awardPointTo(playerId);
        }
    }

    private void winGame(MatchState state, int playerId) {
        awardPoints(state, playerId, 4);
    }

    private void winSetSixToZero(MatchState state, int playerId) {
        for (int i = 0; i < 6; i++) {
            winGame(state, playerId);
        }
    }

    private void reachGamesSixSix(MatchState state) {
        for (int i = 0; i < 5; i++) {
            winGame(state, 1);
            winGame(state, 2);
        }
        winGame(state, 1);
        winGame(state, 2);
    }

    private void startTieBreakAtGamesSixSix(MatchState state) {
        reachGamesSixSix(state);
    }
}