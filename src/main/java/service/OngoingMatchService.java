package service;

import dao.OngoingMatchDao;
import dao.PlayerDao;
import dto.MatchScoreDto;
import exception.ValidationException;
import model.OngoingMatch;
import model.Player;
import model.PlayerSide;
import util.PlayerUtils;
import validation.MatchValidation;
import validation.PlayerValidation;

import java.util.UUID;

public class OngoingMatchService {
    private final OngoingMatchDao ongoingMatchDao;
    private final PlayerDao playerDao;

    public OngoingMatchService(OngoingMatchDao ongoingMatchDao, PlayerDao playerDao) {
        this.ongoingMatchDao = ongoingMatchDao;
        this.playerDao = playerDao;
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

    public OngoingMatch findOngoingMatch(String uuidToConvert) {
        MatchValidation.validateMatchUuid(uuidToConvert);
        UUID uuid = MatchValidation.parseUuid(uuidToConvert);
        return ongoingMatchDao.findByUuid(uuid);
    }

    public String findPlayerNameByOngoingMatch(OngoingMatch ongoingMatch, PlayerSide playerSide) {
        MatchValidation.validateOngoingMatch(ongoingMatch);
        if (playerSide == null) {
            throw new ValidationException("player side cannot be null");
        }

        Integer playerId = switch (playerSide) {
            case PLAYER1 -> ongoingMatch.getPlayer1();
            case PLAYER2 -> ongoingMatch.getPlayer2();
        };
        return findPlayerNameById(playerId);
    }

    public String findPlayerNameById(Integer id) {
        PlayerValidation.validatePlayerId(id);
        Player player = playerDao.findById(id);
        PlayerValidation.validatePlayerForRead(player);
        return player.name();
    }

    public Integer findWinnerPlayerId(String winner, OngoingMatch ongoingMatch) {
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

    public void finishOngoingMatch(String uuidToConvert) {
        MatchValidation.validateMatchUuid(uuidToConvert);
        UUID uuid = MatchValidation.parseUuid(uuidToConvert);
        ongoingMatchDao.removeByUuid(uuid);
    }
}
