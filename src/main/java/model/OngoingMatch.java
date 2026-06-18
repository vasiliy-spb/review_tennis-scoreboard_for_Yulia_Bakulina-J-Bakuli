package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class OngoingMatch {
    private UUID uuid;
    private Integer player1;
    private Integer player2;
    private MatchState matchState;
}
