package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import util.HibernateUtil;

public class H2PlayerDaoTest extends AbstractPlayerDaoTest {
    @BeforeEach
    public void setUp() {
        playerDao = new H2PlayerDao();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from FinishedMatchEntity").executeUpdate();
            session.createMutationQuery("delete from PlayerEntity").executeUpdate();
            tx.commit();
        }
    }
}
