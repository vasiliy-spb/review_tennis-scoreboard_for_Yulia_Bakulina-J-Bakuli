package controller;

import dao.OngoingMatchDao;
import dao.PlayerDao;
import db.AppLifecycleListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import service.NewMatchService;

import java.io.IOException;
import java.util.UUID;

@WebServlet({"/new-match"})
@Slf4j
public class NewMatchServlet extends BaseServlet {
    private NewMatchService newMatchService;

    @Override
    public void init() throws ServletException {
        PlayerDao playerDao = getRequiredAttribute(AppLifecycleListener.PLAYER_DAO_ATTR, PlayerDao.class);
        OngoingMatchDao ongoingMatchDao = getRequiredAttribute(AppLifecycleListener.ONGOING_MATCH_DAO_ATTR, OngoingMatchDao.class);
        newMatchService = new NewMatchService(playerDao, ongoingMatchDao);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /new-match");
        req.getRequestDispatcher("/WEB-INF/views/new-match.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String player1Name = req.getParameter("player1Name");
        String player2Name = req.getParameter("player2Name");

        log.info("POST /new-match - received: player1Name='{}', player2Name='{}'",
                player1Name != null ? player1Name : "null",
                player2Name != null ? player2Name : "null");

        UUID matchId = newMatchService.startNewMatch(player1Name, player2Name);
        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + matchId);
    }
}
