package dao;

import exception.NotFoundException;
import model.MatchState;
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
    public void saveOngoingMatchTest() {
        UUID uuid = UUID.randomUUID();
        MatchState basicMatchState = createbasicMatchState();
        OngoingMatch saved = ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, basicMatchState,null));

        Assertions.assertEquals(uuid, saved.getUuid());
        Assertions.assertEquals(1, saved.getPlayer1());
        Assertions.assertEquals(2, saved.getPlayer2());
    }

    @Test
    public void findByUuidTest() {
        UUID uuid = UUID.randomUUID();
        MatchState basicMatchState = createbasicMatchState();
        ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, basicMatchState, null));

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
    public void removeByUuidTest() {
        UUID uuid = UUID.randomUUID();
        MatchState basicMatchState = createbasicMatchState();
        ongoingMatchDao.save(new OngoingMatch(uuid, 1, 2, basicMatchState, null));

        ongoingMatchDao.removeByUuid(uuid);

        Assertions.assertThrows(
                NotFoundException.class,
                () -> ongoingMatchDao.removeByUuid(uuid)
        );
    }

    private MatchState createbasicMatchState() {
        Integer player1Id = 1;
        Integer player2Id = 2;
        return new MatchState(player1Id, player2Id);
    }
}
