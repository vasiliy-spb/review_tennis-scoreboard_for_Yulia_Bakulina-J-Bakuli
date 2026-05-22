package service;

import exception.ValidationException;
import model.MatchScoreResult;
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
    public void validateNullIdTest() {
        state.setPlayer1Id(null);
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, 1));
        state.setPlayer1Id(1);
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, null));
    }

    @Test
    public void validateFinishedStateTest() {
        state.setFinished(true);
        Assertions.assertThrows(ValidationException.class, () -> service.calculate(state, 1));
    }

    @Test
    public void shouldIncreasePlayer2PointByOneWhenPlayer2WinsTest() {
        MatchScoreResult result = service.calculate(state, 2);
        Assertions.assertEquals(0, result.getState().getPlayer1PointsInGame());
        Assertions.assertEquals(1, result.getState().getPlayer2PointsInGame());
    }

    @Test
    public void shouldAwardGameAndResetGamePointsWhenPlayerWinsFromThreePointsTest() {
        state.setPlayer1PointsInGame(3);
        state.setPlayer2PointsInGame(0);
        MatchScoreResult result = service.calculate(state, 1);
        Assertions.assertEquals(1, result.getState().getPlayer1GamesInSet());
        Assertions.assertEquals(0, result.getState().getPlayer1PointsInGame());
        Assertions.assertEquals(0, result.getState().getPlayer2GamesInSet());
        Assertions.assertEquals(0, result.getState().getPlayer2PointsInGame());
    }

    @Test
    public void advantageLessThan2ShouldNotFinishGameTest() {
        state.setPlayer1PointsInGame(3);
        state.setPlayer2PointsInGame(3);

        MatchScoreResult result = service.calculate(state, 1);

        Assertions.assertEquals(4, result.getState().getPlayer1PointsInGame());
        Assertions.assertEquals(3, result.getState().getPlayer2PointsInGame());
        Assertions.assertEquals(0, result.getState().getPlayer1GamesInSet());
        Assertions.assertEquals(0, result.getState().getPlayer2GamesInSet());
    }

    @Test
    public void shouldAwardGameWhenLeadBecomesTwoAfterAdvantageTest() {
        state.setPlayer1PointsInGame(4);
        state.setPlayer2PointsInGame(3);

        MatchScoreResult result = service.calculate(state, 1);

        Assertions.assertEquals(0, result.getState().getPlayer1PointsInGame());
        Assertions.assertEquals(0, result.getState().getPlayer2PointsInGame());
        Assertions.assertEquals(1, result.getState().getPlayer1GamesInSet());
        Assertions.assertEquals(0, result.getState().getPlayer2GamesInSet());
    }

    @Test
    public void sixSixShouldStartTieBreakTest() {
        state.setPlayer1GamesInSet(6);
        state.setPlayer2GamesInSet(6);

        MatchScoreResult result = service.calculate(state, 1);

        Assertions.assertTrue(result.getState().isTieBreak());
    }

    @Test
    public void tieBreakWinEightToSixShouldAwardSetTest() {
        setTieBreakAtSixSix(state);
        state.setPlayer1TieBreakPoints(6);
        state.setPlayer2TieBreakPoints(6);

        MatchScoreResult resultAfterSevenSix = service.calculate(state, 1);
        Assertions.assertEquals(0, resultAfterSevenSix.getState().getPlayer1Sets());
        Assertions.assertEquals(0, resultAfterSevenSix.getState().getPlayer2Sets());

        MatchScoreResult resultAfterEightSix = service.calculate(state, 1);
        Assertions.assertEquals(1, resultAfterEightSix.getState().getPlayer1Sets());
        Assertions.assertEquals(0, resultAfterEightSix.getState().getPlayer2Sets());

        Assertions.assertFalse(resultAfterEightSix.getState().isTieBreak());
    }

    @Test
    public void tieBreakShouldNotFinishWithoutTwoPointsDifferenceTest() {
        setTieBreakAtSixSix(state);
        state.setPlayer1TieBreakPoints(6);
        state.setPlayer2TieBreakPoints(6);

        MatchScoreResult result = service.calculate(state, 1);

        Assertions.assertTrue(result.getState().isTieBreak());
        Assertions.assertEquals(6, result.getState().getPlayer1GamesInSet());
        Assertions.assertEquals(6, result.getState().getPlayer2GamesInSet());
    }

    @Test
    public void shouldFinishMatchAtTwoSetsToZeroTest() {
        state.setPlayer1Sets(1);
        state.setPlayer1GamesInSet(5);
        state.setPlayer2GamesInSet(0);
        state.setPlayer1PointsInGame(3);
        state.setPlayer2PointsInGame(0);
        MatchScoreResult result = service.calculate(state, 1);
        Assertions.assertTrue(result.isFinished());
        Assertions.assertEquals(1, result.getWinnerPlayerId());
    }

    @Test
    public void shouldFinishMatchAtTwoSetsToOneTest() {
        state.setPlayer1Sets(1);
        state.setPlayer2Sets(1);
        state.setPlayer1GamesInSet(0);
        state.setPlayer2GamesInSet(5);
        state.setPlayer1PointsInGame(0);
        state.setPlayer2PointsInGame(3);

        MatchScoreResult result = service.calculate(state, 2);

        Assertions.assertTrue(result.isFinished());
        Assertions.assertEquals(2, result.getWinnerPlayerId());
        Assertions.assertEquals(2, result.getState().getPlayer2Sets());
        Assertions.assertEquals(1, result.getState().getPlayer1Sets());
    }

    private MatchState basicMatchState() {
        Integer player1Id = 1;
        Integer player2Id = 2;
        return new MatchState(player1Id, player2Id);
    }

    private void setTieBreakAtSixSix(MatchState state) {
        state.setPlayer1GamesInSet(6);
        state.setPlayer2GamesInSet(6);
        state.setTieBreak(true);
    }
}