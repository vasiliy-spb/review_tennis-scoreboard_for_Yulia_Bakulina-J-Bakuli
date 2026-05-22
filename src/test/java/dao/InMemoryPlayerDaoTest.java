package dao;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryPlayerDaoTest extends AbstractPlayerDaoTest {
    @BeforeEach
    public void setUp() {
        playerDao = new InMemoryPlayerDao();
    }
}
