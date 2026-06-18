package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class FinishedMatch {
    private Integer player1Id;
    private Integer player2Id;
    private Integer winnerId;
    private LocalDateTime finishedAt;
}
