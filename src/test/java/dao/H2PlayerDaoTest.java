package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import util.HibernateUtil;

public class H2PlayerDaoTest extends AbstractPlayerDaoTest {
    @BeforeEach
    public void setUp() throws Exception {
        playerDao = new H2PlayerDao();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("delete from FinishedMatchEntity").executeUpdate();
            session.createMutationQuery("delete from PlayerEntity").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
