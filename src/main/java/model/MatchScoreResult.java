package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchScoreResult {
    private MatchState state;
    private boolean finished;
    private Integer winnerPlayerId;
}
