package model;

import lombok.Getter;

@Getter
public class RegularGameScore {
    private GamePoint player1PointsInGame = GamePoint.LOVE;
    private GamePoint player2PointsInGame = GamePoint.LOVE;
    private boolean isGameFinished;

    public void awardPointTo(boolean isPlayer1PointWinner) {
        isGameFinished = false;
        GamePoint winner = getGamePointFor(isPlayer1PointWinner);
        GamePoint loser = getGamePointFor(!isPlayer1PointWinner);

        if (winner == GamePoint.ADVANTAGE) {
            isGameFinished = true;
            return;
        }

        if (winner == GamePoint.FORTY) {
            if (loser == GamePoint.FORTY) {
                setGamePoint(isPlayer1PointWinner, GamePoint.ADVANTAGE);
            } else if (loser == GamePoint.ADVANTAGE) {
                setDeuce();
            } else {
                isGameFinished = true;
            }
            return;
        }
        setGamePoint(isPlayer1PointWinner, winner.nextInGame());
    }

    public boolean isFinished() {
        return isGameFinished;
    }

    public void reset() {
        player1PointsInGame = GamePoint.LOVE;
        player2PointsInGame = GamePoint.LOVE;
        isGameFinished = false;
    }

    private GamePoint getGamePointFor(boolean isPlayer1) {
        return isPlayer1 ? player1PointsInGame : player2PointsInGame;
    }

    private void setGamePoint(boolean isPlayer1, GamePoint value) {
        if (isPlayer1) {
            player1PointsInGame = value;
        } else {
            player2PointsInGame = value;
        }
    }

    private void setDeuce() {
        player1PointsInGame = GamePoint.FORTY;
        player2PointsInGame = GamePoint.FORTY;
    }
}
