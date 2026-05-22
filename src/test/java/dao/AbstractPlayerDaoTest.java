package dao;

import exception.AlreadyExistsException;
import exception.NotFoundException;
import model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractPlayerDaoTest {
    protected PlayerDao playerDao;

    @Test
    public void createPlayerTest() {
        Player saved = playerDao.save(new Player(null, "  Sharapova  "));
        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("sharapova", saved.getName());

        playerDao.save(new Player(null, "Nadal"));
        Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> playerDao.save(new Player(null, "  Nadal  "))
        );
    }

    @Test
    public void findByNameTest() {
        Player created = playerDao.save(new Player(null, "Safin"));
        Player found1 = playerDao.findByName("safin");
        Player found2 = playerDao.findByName("  SAFIN  ");
        Assertions.assertEquals(created.getId(), found1.getId());
        Assertions.assertEquals(created.getId(), found2.getId());
        Assertions.assertEquals("safin", found1.getName());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> playerDao.findByName("Nadal")
        );
    }

    @Test
    public void findByIdTest() {
        Player created = playerDao.save(new Player(null, "Safin"));
        Player found = playerDao.findById(created.getId());

        Assertions.assertEquals(created.getId(), found.getId());
        Assertions.assertEquals("safin", found.getName());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> playerDao.findById(76)
        );
    }
}
