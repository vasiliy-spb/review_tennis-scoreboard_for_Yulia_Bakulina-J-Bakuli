package mapper;

import dto.FinishedMatchDto;
import persistence.entity.FinishedMatchEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public final class FinishedMatchDtoMapper {
    private static final DateTimeFormatter FINISHED_AT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private FinishedMatchDtoMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static FinishedMatchDto toDto(FinishedMatchEntity finishedMatchEntity) {
        LocalDateTime finishedAt = finishedMatchEntity.getFinishedAt();
        String finishedAtFormatted = finishedAt.format(FINISHED_AT_FORMATTER);
        return new FinishedMatchDto(
                finishedMatchEntity.getPlayer1().getName(),
                finishedMatchEntity.getPlayer2().getName(),
                finishedMatchEntity.getWinner().getName(),
                finishedAt,
                finishedAtFormatted
        );
    }

    public static List<FinishedMatchDto> toDto(List<FinishedMatchEntity> finishedMatchEntities) {
        return finishedMatchEntities.stream()
                .map(FinishedMatchDtoMapper::toDto).toList();
    }
}
