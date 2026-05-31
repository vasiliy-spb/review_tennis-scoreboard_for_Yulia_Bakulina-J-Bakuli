package dao;

import model.FinishedMatch;
import model.MatchScoreResult;
import model.OngoingMatch;

import java.util.UUID;

public interface MatchesDao {
    FinishedMatch save(OngoingMatch match);
    FinishedMatch findAll(UUID uuid);
    Integer countAll(UUID uuid);
}
