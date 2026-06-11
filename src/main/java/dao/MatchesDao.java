package dao;

import model.FinishedMatch;
import model.OngoingMatch;

import java.util.List;

public interface MatchesDao {
    FinishedMatch save(OngoingMatch match);

    List<FinishedMatch> findAllMatches();

    List<FinishedMatch> findAllMatches(int offset, int limit);

    List<FinishedMatch> findMatchesByPlayerName(String playerName, int offset, int limit);

    Integer countAllMatches();

    Integer countMatchesByPlayerName(String playerName);
}
