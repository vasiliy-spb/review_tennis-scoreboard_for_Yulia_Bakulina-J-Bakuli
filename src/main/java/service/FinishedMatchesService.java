package service;

import dao.MatchesDao;
import dto.FinishedMatchDto;
import dto.FinishedMatchesPageDto;
import model.OngoingMatch;
import util.MatchesQueryUtils;
import validation.MatchValidation;
import validation.MatchesQueryValidation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FinishedMatchesService {
    private static final String MATCHES_PATH = "/matches";
    private static final int PAGE_SIZE = 10;
    private final MatchesDao matchesDao;

    public FinishedMatchesService(MatchesDao matchesDao) {
        this.matchesDao = matchesDao;
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
                        matchesDao.findAllMatches(offset, PAGE_SIZE) :
                        matchesDao.findMatchesByPlayerName(playerNameFilter, offset, PAGE_SIZE);

        String previousPageUrl = page > 1 ? buildPageUrl(page - 1, playerNameParam) : null;
        String nextPageUrl = page < totalPages ? buildPageUrl(page + 1, playerNameParam) : null;

        return new FinishedMatchesPageDto(matches, page, totalPages, playerNameParam, previousPageUrl, nextPageUrl);
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
