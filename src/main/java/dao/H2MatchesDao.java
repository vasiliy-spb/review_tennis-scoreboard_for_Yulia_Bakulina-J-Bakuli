package dao;

import dto.FinishedMatchDto;
import exception.AlreadyExistsException;
import exception.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import mapper.FinishedMatchDtoMapper;
import mapper.H2FinishedMatchMapper;
import model.FinishedMatch;
import model.MatchState;
import model.OngoingMatch;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mapstruct.factory.Mappers;
import persistence.entity.FinishedMatchEntity;
import persistence.entity.PlayerEntity;
import util.HibernateUtil;
import util.PlayerUtils;
import validation.MatchValidation;

import java.util.List;

@Slf4j
public class H2MatchesDao extends AbstractH2Dao implements MatchesDao {
    private final FinishedMatchDtoMapper mapper = Mappers.getMapper(FinishedMatchDtoMapper.class);
    private static final String WHERE_BY_PLAYER_PATTERN =
            "WHERE (:pattern IS NULL " +
                    "OR LOWER(p1.name) LIKE LOWER(:pattern) " +
                    "OR LOWER(p2.name) LIKE LOWER(:pattern)) ";

    private static final String COUNT_ALL_MATCHES_BY_PLAYER_NAME =
            "SELECT COUNT (m) FROM FinishedMatchEntity m " +
                    "JOIN m.player1 p1 " +
                    "JOIN m.player2 p2 " +
                    WHERE_BY_PLAYER_PATTERN;

    private static final String FIND_ALL_MATCHES_BY_PLAYER_NAME =
            "SELECT m FROM FinishedMatchEntity m " +
                    "JOIN FETCH m.player1 p1 " +
                    "JOIN FETCH m.player2 p2 " +
                    "JOIN FETCH m.winner " +
                    WHERE_BY_PLAYER_PATTERN +
                    "ORDER BY m.finishedAt DESC";

    @Override
    public FinishedMatch save(OngoingMatch ongoingMatch) {
        MatchValidation.validateOngoingMatch(ongoingMatch);

        log.debug("Saving ongoingMatch as it is finished: uuid={}, player1={}, player2={}, matchState={}, winner={}", ongoingMatch.getUuid(),
                ongoingMatch.getPlayer1(), ongoingMatch.getPlayer2(), ongoingMatch.getMatchState(), ongoingMatch.getWinner());

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            MatchState matchState = ongoingMatch.getMatchState();
            PlayerEntity player1 = session.getReference(PlayerEntity.class, ongoingMatch.getPlayer1());
            PlayerEntity player2 = session.getReference(PlayerEntity.class, ongoingMatch.getPlayer2());
            PlayerEntity winner = session.getReference(PlayerEntity.class, matchState.getWinnerPlayerId());
            FinishedMatchEntity finishedMatchEntity = H2FinishedMatchMapper.toEntity(player1, player2, winner);
            session.persist(finishedMatchEntity);
            tx.commit();

            return H2FinishedMatchMapper.toFinishedMatch(finishedMatchEntity);
        } catch (Exception e) {
            rollbackSafely(tx, e);

            if (isDuplicate(e)) {
                throw new AlreadyExistsException("Finished match already exists.", e);
            }

            throw new DatabaseException("Failed to save finished match", e);
        }
    }

    @Override
    public Integer countAllMatches() {
        log.debug("Counting all finished matches");
        return countByPattern(null);
    }

    @Override
    public List<FinishedMatchDto> findAllMatches(int offset, int limit) {
        log.debug("Finding all finished matches with offset {} and limit {}", offset, limit);
        return findByPattern(null, offset, limit);
    }

    @Override
    public List<FinishedMatchDto> findMatchesByPlayerName(String playerName, int offset, int limit) {
        log.debug("Finding finished matches by {} with offset {} and limit {}", playerName, offset, limit);
        String pattern = bringToPattern(playerName);
        return findByPattern(pattern, offset, limit);
    }

    @Override
    public Integer countMatchesByPlayerName(String playerName) {
        log.debug("Counting all finished matches by {}", playerName);
        String pattern = bringToPattern(playerName);
        return countByPattern(pattern);
    }

    private Integer countByPattern(String pattern) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(COUNT_ALL_MATCHES_BY_PLAYER_NAME, Long.class)
                    .setParameter("pattern", pattern)
                    .getSingleResult()
                    .intValue();
        } catch (Exception e) {
            throw new DatabaseException("Failed to count finished matches", e);
        }
    }

    private List<FinishedMatchDto> findByPattern(String pattern, int offset, int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<FinishedMatchEntity> matchEntities = session.createQuery(FIND_ALL_MATCHES_BY_PLAYER_NAME, FinishedMatchEntity.class)
                    .setParameter("pattern", pattern)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
            return mapper.toDto(matchEntities);
        } catch (Exception e) {
            throw new DatabaseException("Failed to find finished matches", e);
        }
    }

    private String bringToPattern(String playerName) {
        return "%" + PlayerUtils.normalizeInput(playerName) + "%";
    }
}
