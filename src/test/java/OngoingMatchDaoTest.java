import dao.InMemoryOngoingMatchDao;
import dao.OngoingMatchDao;
import exception.NotFoundException;
import exception.ValidationException;
import model.OngoingMatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class OngoingMatchDaoTest {
    private OngoingMatchDao ongoingMatchDao;

    @BeforeEach
    public void setUp() {
        ongoingMatchDao = new InMemoryOngoingMatchDao();
    }

    @Test
    void saveOngoingMatchTest() {
        UUID uuid = UUID.randomUUID();
        OngoingMatch saved = ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, null));

        Assertions.assertEquals(uuid, saved.getUuid());
        Assertions.assertEquals(1, saved.getPlayer1());
        Assertions.assertEquals(2, saved.getPlayer2());
    }

    @Test
    void findByUuidTest() {
        UUID uuid = UUID.randomUUID();
        ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, null));

        OngoingMatch found = ongoingMatchDao.findByUuid(uuid);
        Assertions.assertEquals(uuid, found.getUuid());
        Assertions.assertEquals(1, found.getPlayer1());
        Assertions.assertEquals(2, found.getPlayer2());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> ongoingMatchDao.findByUuid(UUID.randomUUID())
        );
    }

    @Test
    void removeByUuidTest() {
        UUID uuid = UUID.randomUUID();
        ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, null));

        ongoingMatchDao.removeByUuid(uuid);

        Assertions.assertThrows(
                NotFoundException.class,
                () -> ongoingMatchDao.removeByUuid(uuid)
        );
    }
}
