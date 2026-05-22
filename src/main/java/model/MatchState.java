package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchState {
    private Integer player1Id;
    private Integer player2Id;
    private int player1Sets;
    private int player2Sets;
    private int player1GamesInSet;
    private int player2GamesInSet;
    private int player1PointsInGame;
    private int player2PointsInGame;
    private boolean tieBreak;
    private int player1TieBreakPoints;
    private int player2TieBreakPoints;
    private boolean finished;
    private Integer winnerPlayerId;

    public MatchState(Integer player1Id, Integer player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Sets = 0;
        this.player2Sets = 0;
        this.player1GamesInSet = 0;
        this.player2GamesInSet = 0;
        this.player1PointsInGame = 0;
        this.player2PointsInGame = 0;
        this.tieBreak = false;
        this.player1TieBreakPoints = 0;
        this.player2TieBreakPoints = 0;
        this.finished = false;
        this.winnerPlayerId = null;
    }
}
