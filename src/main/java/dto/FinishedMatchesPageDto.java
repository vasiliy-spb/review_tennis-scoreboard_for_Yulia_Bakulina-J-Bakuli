package dto;

import java.util.List;

public record FinishedMatchesPageDto(
        List<FinishedMatchDto> matches,
        int currentPage,
        int totalPages,
        String filterByPlayerName,
        boolean hasPrevious,
        boolean hasNext
) {
}
