package controller;

import dao.MatchesDao;
import dao.OngoingMatchDao;
import dao.PlayerDao;
import db.AppLifecycleListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import model.MatchScoreResult;
import model.MatchState;
import model.OngoingMatch;
import model.PlayerSide;
import service.FinishedMatchesService;
import service.MatchScoreCalculationService;
import service.OngoingMatchService;

import java.io.IOException;

@WebServlet({"/match-score"})
@Slf4j
public class MatchScoreServlet extends BaseServlet {
    private OngoingMatchService ongoingMatchService;
    private FinishedMatchesService finishedMatchesService;
    private MatchScoreCalculationService matchScoreCalculationService;

    @Override
    public void init() throws ServletException {
        PlayerDao playerDao = getRequiredAttribute(AppLifecycleListener.PLAYER_DAO_ATTR, PlayerDao.class);
        MatchesDao matchesDao = getRequiredAttribute(AppLifecycleListener.MATCHES_DAO_ATTR, MatchesDao.class);
        OngoingMatchDao ongoingMatchDao = getRequiredAttribute(AppLifecycleListener.ONGOING_MATCH_DAO_ATTR, OngoingMatchDao.class);
        ongoingMatchService = new OngoingMatchService(ongoingMatchDao, playerDao);
        finishedMatchesService = new FinishedMatchesService(matchesDao);
        matchScoreCalculationService = new MatchScoreCalculationService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /match-score");
        String uuid = req.getParameter("uuid");

        OngoingMatch ongoingMatch = ongoingMatchService.findOngoingMatch(uuid);
        req.setAttribute("uuid", ongoingMatch.getUuid());
        req.setAttribute("matchState", ongoingMatch.getMatchState());
        req.setAttribute("player1Name", ongoingMatchService.findPlayerNameByOngoingMatch(ongoingMatch, PlayerSide.PLAYER1));
        req.setAttribute("player2Name", ongoingMatchService.findPlayerNameByOngoingMatch(ongoingMatch, PlayerSide.PLAYER2));
        req.setAttribute("player1", ongoingMatch.getPlayer1());
        req.setAttribute("player2", ongoingMatch.getPlayer2());
        req.setAttribute("winner", ongoingMatch.getWinner());
        req.getRequestDispatcher("/WEB-INF/views/match-score.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("POST /match-score");
        String uuid = req.getParameter("uuid");
        String winner = req.getParameter("winner");

        OngoingMatch ongoingMatch = ongoingMatchService.findOngoingMatch(uuid);
        Integer winnerId = ongoingMatchService.findWinnerPlayerId(winner, ongoingMatch);
        MatchState matchState = ongoingMatch.getMatchState();
        MatchScoreResult matchScoreResult;

        if (matchState.isFinished()) {
            resp.sendRedirect(req.getContextPath() + "/matches");
            return;
        } else {
            matchScoreResult = matchScoreCalculationService.calculate(matchState, winnerId);
        }

        if (matchScoreResult.isFinished()) {
            finishedMatchesService.saveFinishedMatch(ongoingMatch);
            ongoingMatchService.finishOngoingMatch(uuid);
            resp.sendRedirect(req.getContextPath() + "/matches");
        } else {
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
        }
    }
}
