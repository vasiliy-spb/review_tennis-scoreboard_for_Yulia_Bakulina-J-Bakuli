package mapper;

import dto.FinishedMatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import persistence.entity.FinishedMatchEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Mapper
public interface FinishedMatchDtoMapper {
    @Mapping(target = "player1Name", source = "player1.name")
    @Mapping(target = "player2Name", source = "player2.name")
    @Mapping(target = "winnerName", source = "winner.name")
    @Mapping(target = "finishedAtFormatted", expression = "java(formatFinishedAt(entity.getFinishedAt()))")
    FinishedMatchDto toDto(FinishedMatchEntity entity);

    List<FinishedMatchDto> toDto(List<FinishedMatchEntity> dtos);

    default String formatFinishedAt(LocalDateTime finishedAt) {
        return finishedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
