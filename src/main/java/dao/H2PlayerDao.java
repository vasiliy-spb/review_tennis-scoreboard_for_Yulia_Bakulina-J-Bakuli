package dao;

import exception.AlreadyExistsException;
import exception.DataAccessException;
import exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import mapper.H2PlayerMapper;
import model.Player;
import persistence.entity.PlayerEntity;
import util.PlayerUtils;

@Slf4j
public class H2PlayerDao extends AbstractH2Dao implements PlayerDao {
    private static final String SELECT_BY_NAME = "from PlayerEntity p where p.name = :name";
    private static final String SELECT_BY_ID = "from PlayerEntity p where p.id = :id";

    @Override
    public Player save(Player player) {
        String normalizedName = PlayerUtils.normalizeInput(player.name());

        log.debug("Saving player: id={}, name={} ", player.id(), normalizedName);

        try {
            PlayerEntity playerEntity = H2PlayerMapper.toEntity(normalizedName);
            session().persist(playerEntity);
            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (Exception e) {
            if (isDuplicate(e)) {
                throw new AlreadyExistsException("Player with name=" + normalizedName + " already exists.", e);
            }

            throw new DataAccessException("Failed to save player with name=" + normalizedName, e);
        }
    }

    @Override
    public Player findByName(String name) {
        String normalizedName = PlayerUtils.normalizeInput(name);

        log.debug("Finding player by name: name={} ", normalizedName);

        try {
            PlayerEntity playerEntity = session().createQuery(SELECT_BY_NAME, PlayerEntity.class)
                    .setParameter("name", normalizedName)
                    .uniqueResult();

            if (playerEntity == null) {
                throw new NotFoundException("Player not found by name=" + normalizedName);
            }

            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Failed to find player by name=" + normalizedName, e);
        }
    }

    @Override
    public Player findById(Integer id) {
        log.debug("Finding player by id: id={} ", id);

        try {
            PlayerEntity playerEntity = session().createQuery(SELECT_BY_ID, PlayerEntity.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (playerEntity == null) {
                throw new NotFoundException("Player not found by id=" + id);
            }

            return H2PlayerMapper.toPlayer(playerEntity);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Failed to find player by id=" + id, e);
        }
    }
}
