package db;

import dao.H2MatchesDao;
import dao.H2PlayerDao;
import dao.InMemoryOngoingMatchDao;
import dao.MatchesDao;
import dao.OngoingMatchDao;
import dao.PlayerDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import service.FinishedMatchesService;
import service.MatchScoreCalculationService;
import service.NewMatchService;
import service.OngoingMatchService;
import util.HibernateUtil;

@Slf4j
@WebListener
public class AppLifecycleListener implements ServletContextListener {
    public static final String ONGOING_MATCH_DAO_ATTR = "ongoingMatchDao";
    public static final String PLAYER_DAO_ATTR = "playerDao";
    public static final String MATCHES_DAO_ATTR = "matchesDao";
    public static final String NEW_MATCH_SERVICE_ATTR = "newMatchService";
    public static final String ONGOING_MATCH_SERVICE_ATTR = "ongoingMatchService";
    public static final String FINISHED_MATCHES_SERVICE_ATTR = "finishedMatchesService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Application context initialization started");
        try {
            SessionFactory ignored = HibernateUtil.getSessionFactory();
            OngoingMatchDao ongoingMatchDao = new InMemoryOngoingMatchDao();
            PlayerDao playerDao = new H2PlayerDao();
            MatchesDao matchesDao = new H2MatchesDao();
            MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService();
            FinishedMatchesService finishedMatchesService = new FinishedMatchesService(matchesDao);
            NewMatchService newMatchService = new NewMatchService(playerDao, ongoingMatchDao);
            OngoingMatchService ongoingMatchService = new OngoingMatchService(
                    matchScoreCalculationService, finishedMatchesService, ongoingMatchDao, playerDao);

            sce.getServletContext().setAttribute(ONGOING_MATCH_DAO_ATTR, ongoingMatchDao);
            sce.getServletContext().setAttribute(PLAYER_DAO_ATTR, playerDao);
            sce.getServletContext().setAttribute(MATCHES_DAO_ATTR, matchesDao);
            sce.getServletContext().setAttribute(NEW_MATCH_SERVICE_ATTR, newMatchService);
            sce.getServletContext().setAttribute(ONGOING_MATCH_SERVICE_ATTR, ongoingMatchService);
            sce.getServletContext().setAttribute(FINISHED_MATCHES_SERVICE_ATTR, finishedMatchesService);
            log.info("Hibernate SessionFactory, DAO and services initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize application components", e);
            throw e;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application context destroy started, closing SessionFactory");
        try {
            HibernateUtil.shutdown();
            log.info("Hibernate SessionFactory is closed");
        } catch (Exception e) {
            log.error("Failed to close Hibernate SessionFactory", e);
        }
    }
}
