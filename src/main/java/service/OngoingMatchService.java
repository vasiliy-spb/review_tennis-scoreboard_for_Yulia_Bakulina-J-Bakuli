package service;

import dao.OngoingMatchDao;
import dao.PlayerDao;
import dto.MatchScoreDto;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.MatchState;
import model.OngoingMatch;
import model.Player;
import util.PlayerUtils;
import validation.MatchValidation;
import validation.PlayerValidation;

import java.util.UUID;


@Slf4j
public class OngoingMatchService {
    private final MatchScoreCalculationService matchScoreCalculationService;
    private final FinishedMatchesService finishedMatchesService;
    private final OngoingMatchDao ongoingMatchDao;
    private final PlayerDao playerDao;

    public OngoingMatchService(MatchScoreCalculationService matchScoreCalculationService,
                               FinishedMatchesService finishedMatchesService,
                               OngoingMatchDao ongoingMatchDao,
                               PlayerDao playerDao) {
        this.matchScoreCalculationService = matchScoreCalculationService;
        this.finishedMatchesService = finishedMatchesService;
        this.ongoingMatchDao = ongoingMatchDao;
        this.playerDao = playerDao;
    }

    public boolean processMatchScore(String uuidToConvert, String pointWinner) {
        OngoingMatch ongoingMatch = findOngoingMatch(uuidToConvert);
        MatchState matchState = ongoingMatch.getMatchState();
        Integer pointWinnerId = findWinnerPlayerId(pointWinner, ongoingMatch);
        UUID uuid = ongoingMatch.getUuid();

        synchronized (ongoingMatch) {
            try {
                if (matchState.isFinished()) {
                    return true;
                } else {
                    matchScoreCalculationService.calculate(matchState, pointWinnerId);
                }

                if (matchState.isFinished()) {
                    finishedMatchesService.saveFinishedMatch(ongoingMatch);
                    finishOngoingMatch(uuid.toString());
                    return true;
                }
            } catch (NotFoundException e) {
                log.info("Match uuid={} is already finished and removed from memory", uuid);
                return true;
            }
        }
        return false;
    }

    public MatchScoreDto findMatchScore(String uuidToConvert) {
        OngoingMatch ongoingMatch = findOngoingMatch(uuidToConvert);
        return new MatchScoreDto(
                ongoingMatch.getUuid().toString(),
                findPlayerNameById(ongoingMatch.getPlayer1()),
                findPlayerNameById(ongoingMatch.getPlayer2()),
                ongoingMatch.getMatchState().getPlayer1Sets(),
                ongoingMatch.getMatchState().getPlayer2Sets(),
                ongoingMatch.getMatchState().getPlayer1GamesInSet(),
                ongoingMatch.getMatchState().getPlayer2GamesInSet(),
                ongoingMatch.getMatchState().getPlayer1PointsDisplay(),
                ongoingMatch.getMatchState().getPlayer2PointsDisplay()
        );
    }

    private OngoingMatch findOngoingMatch(String uuidToConvert) {
        MatchValidation.validateMatchUuid(uuidToConvert);
        UUID uuid = MatchValidation.parseUuid(uuidToConvert);
        return ongoingMatchDao.findByUuid(uuid);
    }

    private String findPlayerNameById(Integer id) {
        PlayerValidation.validatePlayerId(id);
        Player player = playerDao.findById(id);
        PlayerValidation.validatePlayerForRead(player);
        return player.name();
    }

    private Integer findWinnerPlayerId(String winner, OngoingMatch ongoingMatch) {
        MatchValidation.validateWinner(winner);
        MatchValidation.validateOngoingMatch(ongoingMatch);

        Integer player1Id = ongoingMatch.getPlayer1();
        Integer player2Id = ongoingMatch.getPlayer2();

        String normalizedWinner = PlayerUtils.normalizeInput(winner);

        if ("player1".equals(normalizedWinner)) {
            return player1Id;
        }
        return player2Id;
    }

    private void finishOngoingMatch(String uuidToConvert) {
        MatchValidation.validateMatchUuid(uuidToConvert);
        UUID uuid = MatchValidation.parseUuid(uuidToConvert);
        ongoingMatchDao.removeByUuid(uuid);
    }
}
