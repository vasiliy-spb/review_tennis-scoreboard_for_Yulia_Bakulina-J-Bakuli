package service;

import dao.OngoingMatchDao;
import model.OngoingMatch;
import util.PlayerUtils;
import validation.MatchValidation;

import java.util.UUID;

public class OngoingMatchService {
    private final OngoingMatchDao ongoingMatchDao;

    public OngoingMatchService(OngoingMatchDao ongoingMatchDao) {
        this.ongoingMatchDao = ongoingMatchDao;
    }

    public OngoingMatch findOngoingMatch(String uuidToConvert) {
        MatchValidation.validateMatchUuid(uuidToConvert);
        UUID uuid = UUID.fromString(uuidToConvert);
        return ongoingMatchDao.findByUuid(uuid);
    } //Todo добавить validate MatchState и валидировать его в ongoing

    public Integer findWinnerPlayerId(String winner, OngoingMatch ongoingMatch) {
        MatchValidation.validateWinner(winner);

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
        UUID uuid = UUID.fromString(uuidToConvert);
        ongoingMatchDao.removeByUuid(uuid);
    }

    public void saveFinishedMatch(String uuidToConvert) {

    }
}
