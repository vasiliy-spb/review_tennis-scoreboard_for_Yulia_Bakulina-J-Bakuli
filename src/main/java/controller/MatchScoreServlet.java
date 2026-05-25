package controller;

import dao.OngoingMatchDao;
import db.AppLifecycleListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import model.MatchScoreResult;
import model.MatchState;
import model.OngoingMatch;
import service.MatchScoreCalculationService;
import service.OngoingMatchService;

import java.io.IOException;

@WebServlet({"/match-score"})
@Slf4j
public class MatchScoreServlet extends HttpServlet {
    private OngoingMatchService ongoingMatchService;
    private MatchScoreCalculationService matchScoreCalculationService;

    @Override
    public void init() throws ServletException {
        OngoingMatchDao ongoingMatchDao = (OngoingMatchDao) getServletContext().getAttribute(AppLifecycleListener.ONGOING_MATCH_DAO_ATTR);
        if (ongoingMatchDao == null) {
            throw new ServletException("OngoingMatchDao is not initialized in ServletContext");
        }
        ongoingMatchService = new OngoingMatchService(ongoingMatchDao);
        matchScoreCalculationService = new MatchScoreCalculationService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /match-score");
        String uuid = req.getParameter("uuid");

        OngoingMatch ongoingMatch = ongoingMatchService.findOngoingMatch(uuid);
        req.setAttribute("uuid", ongoingMatch.getUuid());
        req.setAttribute("matchState", ongoingMatch.getMatchState());
        req.setAttribute("player1", ongoingMatch.getPlayer1());
        req.setAttribute("player2", ongoingMatch.getPlayer2());
        req.setAttribute("winner", ongoingMatch.getWinner());
        req.getRequestDispatcher("/WEB-INF/views/match-score.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("POST /match-score");
        String uuid = req.getParameter("uuid");
        String winner = req.getParameter("winner");

        OngoingMatch ongoingMatch = ongoingMatchService.findOngoingMatch(uuid);
        Integer winnerId = ongoingMatchService.findWinnerPlayerId(winner, ongoingMatch);
        MatchState matchState = ongoingMatch.getMatchState();

        MatchScoreResult matchScoreResult = matchScoreCalculationService.calculate(matchState, winnerId);

        if (matchScoreResult.isFinished()) {
            ongoingMatchService.finishOngoingMatch(uuid);
            ongoingMatchService.saveFinishedMatch(uuid);
            resp.sendRedirect(req.getContextPath() + "/matches");
        } else {
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
        }
    }
}
