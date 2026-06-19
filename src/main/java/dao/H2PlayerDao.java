package dao;

import exception.AlreadyExistsException;
import exception.DatabaseException;
import exception.NotFoundException;
import exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import mapper.H2PlayerMapper;
import model.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.entity.PlayerEntity;
import util.HibernateUtil;
import util.PlayerUtils;
import validation.PlayerValidation;

@Slf4j
public class H2PlayerDao extends AbstractH2Dao implements PlayerDao {
    private static final String SELECT_BY_NAME = "from PlayerEntity p where p.name = :name";
    private static final String SELECT_BY_ID = "from PlayerEntity p where p.id = :id";

    @Override
    public Player save(Player player) {
        PlayerValidation.validatePlayerForCreate(player);

        String normalizedName = PlayerUtils.normalizeName(player.name());

        log.debug("Saving player: id={}, name={} ", player.id(), normalizedName);

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            PlayerEntity playerEntity = H2PlayerMapper.toEntity(normalizedName);

            session.persist(playerEntity);
            tx.commit();

            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (Exception e) {
            rollbackSafely(tx, e);

            if (isDuplicate(e)) {
                throw new AlreadyExistsException("Player with name=" + normalizedName + " already exists.", e);
            }

            throw new DatabaseException("Failed to save player with name=" + normalizedName, e);
        }
    }

    @Override
    public Player findByName(String name) {
        PlayerValidation.validatePlayerName(name);
        String normalizedName = PlayerUtils.normalizeName(name);

        log.debug("Finding player by name: name={} ", normalizedName);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            PlayerEntity playerEntity = session.createQuery(SELECT_BY_NAME, PlayerEntity.class)
                    .setParameter("name", normalizedName)
                    .uniqueResult();

            if (playerEntity == null) {
                throw new NotFoundException("Player not found by name=" + normalizedName);
            }

            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to find player by name=" + normalizedName, e);
        }
    }

    @Override
    public Player findById(Integer id) {
        PlayerValidation.validatePlayerId(id);

        log.debug("Finding player by id: id={} ", id);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            PlayerEntity playerEntity = session.createQuery(SELECT_BY_ID, PlayerEntity.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (playerEntity == null) {
                throw new NotFoundException("Player not found by id=" + id);
            }

            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to find player by id=" + id, e);
        }
    }
}
