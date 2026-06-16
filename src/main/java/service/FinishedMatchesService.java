package service;

import dao.MatchesDao;
import dao.PlayerDao;
import dto.FinishedMatchDto;
import dto.FinishedMatchesPageDto;
import exception.DatabaseException;
import exception.NotFoundException;
import model.FinishedMatch;
import model.OngoingMatch;
import util.MatchesQueryUtils;
import validation.MatchValidation;
import validation.MatchesQueryValidation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class FinishedMatchesService {
    private static final DateTimeFormatter FINISHED_AT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String MATCHES_PATH = "/matches";
    private static final int PAGE_SIZE = 10;
    private final MatchesDao matchesDao;
    private final PlayerDao playerDao;

    public FinishedMatchesService(MatchesDao matchesDao, PlayerDao playerDao) {
        this.matchesDao = matchesDao;
        this.playerDao = playerDao;
    }

    public void saveFinishedMatch(OngoingMatch ongoingMatch) {
        MatchValidation.validateOngoingMatch(ongoingMatch);
        matchesDao.save(ongoingMatch);
    }

    public FinishedMatchesPageDto getFinishedMatchesPage(String pageParam, String playerNameParam) {
        int page = MatchesQueryValidation.parsePage(pageParam);
        String playerNameFilter = MatchesQueryUtils.normalizeFilter(playerNameParam);
        int totalCount = playerNameFilter == null ? matchesDao.countAllMatches() : matchesDao.countMatchesByPlayerName(playerNameFilter);
        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / PAGE_SIZE);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }
        int offset = getOffset(page);
        List<FinishedMatchDto> matches =
                playerNameFilter == null ?
                        toDto(matchesDao.findAllMatches(offset, PAGE_SIZE)) :
                        toDto(matchesDao.findMatchesByPlayerName(playerNameFilter, offset, PAGE_SIZE));

        String previousPageUrl = page > 1 ? buildPageUrl(page - 1, playerNameParam) : null;
        String nextPageUrl = page < totalPages ? buildPageUrl(page + 1, playerNameParam) : null;

        return new FinishedMatchesPageDto(matches, page, totalPages, playerNameParam, previousPageUrl, nextPageUrl);
    }

    private List<FinishedMatchDto> toDto(List<FinishedMatch> finishedMatches) {
        if (finishedMatches.isEmpty()) {
            return Collections.emptyList();
        }

        return finishedMatches.stream()
                .map(match -> {
                    LocalDateTime finishedAt = match.getFinishedAt();
                    String finishedAtFormatted = finishedAt.format(FINISHED_AT_FORMATTER);
                    String player1Name = resolvePlayerNameById(match.getPlayer1Id(), "player1");
                    String player2Name = resolvePlayerNameById(match.getPlayer2Id(), "player2");
                    String winnerName = resolvePlayerNameById(match.getWinnerId(), "winner");

                    return new FinishedMatchDto(
                            player1Name,
                            player2Name,
                            winnerName,
                            finishedAt,
                            finishedAtFormatted
                    );
                })
                .toList(); //Todo оптимизировать позже
    }

    private String resolvePlayerNameById(Integer playerId, String role) {
        try {
            return playerDao.findById(playerId).getName();
        } catch (NotFoundException e) {
            throw new DatabaseException(
                    String.format("Failed to resolve %s name for finished match, playerId=%s", role, playerId), e);
        }
    }

    private String buildPageUrl(int targetPage, String playerNameParam) {
        StringBuilder url = new StringBuilder(MATCHES_PATH)
                .append("?page=")
                .append(targetPage);

        if (playerNameParam != null && !playerNameParam.isBlank()) {
            url.append("&filter_by_player_name=")
                    .append(URLEncoder.encode(playerNameParam.trim(), StandardCharsets.UTF_8));
        }

        return url.toString();
    }

    private int getOffset(int page) {
        return (page - 1) * PAGE_SIZE;
    }
}
