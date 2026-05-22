package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class OngoingMatch {
    private UUID uuid;
    private Integer player1;
    private Integer player2;
    private MatchState matchState;
    private Integer winner;
}
