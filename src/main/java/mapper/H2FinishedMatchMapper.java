package mapper;

import model.FinishedMatch;
import model.OngoingMatch;
import persistence.entity.FinishedMatchEntity;
import persistence.entity.PlayerEntity;
import validation.MatchValidation;

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

    public static FinishedMatchEntity toEntity(OngoingMatch ongoingMatch) {
        MatchValidation.validateOngoingMatch(ongoingMatch);

        FinishedMatchEntity entity = new FinishedMatchEntity();
        PlayerEntity player1 = H2PlayerMapper.toEntityById(ongoingMatch.getPlayer1());
        PlayerEntity player2 = H2PlayerMapper.toEntityById(ongoingMatch.getPlayer2());
        PlayerEntity winner = H2PlayerMapper.toEntityById(ongoingMatch.getWinner());
        LocalDateTime localDateTime = LocalDateTime.now();

        entity.setPlayer1(player1);
        entity.setPlayer2(player2);
        entity.setWinner(winner);
        entity.setFinishedAt(localDateTime);
        return entity;
    }
}
