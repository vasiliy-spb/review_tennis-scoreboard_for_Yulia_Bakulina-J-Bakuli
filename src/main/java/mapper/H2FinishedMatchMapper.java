package mapper;

import model.FinishedMatch;
import persistence.entity.FinishedMatchEntity;
import persistence.entity.PlayerEntity;

import java.time.LocalDateTime;

public final class H2FinishedMatchMapper {
    private H2FinishedMatchMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static FinishedMatch toFinishedMatch(FinishedMatchEntity finishedMatchEntity) {
        return new FinishedMatch(
                finishedMatchEntity.getPlayer1().getId(),
                finishedMatchEntity.getPlayer2().getId(),
                finishedMatchEntity.getWinner().getId(),
                finishedMatchEntity.getFinishedAt());
    }

    public static FinishedMatchEntity toEntity(PlayerEntity player1, PlayerEntity player2, PlayerEntity winner) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new FinishedMatchEntity(player1, player2, winner, localDateTime);
    }
}
