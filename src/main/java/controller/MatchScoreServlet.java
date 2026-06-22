package controller;

import db.AppLifecycleListener;
import dto.MatchScoreDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import service.OngoingMatchService;

import java.io.IOException;

@WebServlet({"/match-score"})
@Slf4j
public class MatchScoreServlet extends BaseServlet {
    private OngoingMatchService ongoingMatchService;

    @Override
    public void init() throws ServletException {
        ongoingMatchService = getRequiredAttribute(
                AppLifecycleListener.ONGOING_MATCH_SERVICE_ATTR, OngoingMatchService.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /match-score");
        String uuid = req.getParameter("uuid");
        MatchScoreDto matchScore = ongoingMatchService.findMatchScore(uuid);
        req.setAttribute("matchScore", matchScore);
        req.getRequestDispatcher("/WEB-INF/views/match-score.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("POST /match-score");
        String uuid = req.getParameter("uuid");
        String winner = req.getParameter("winner");
        boolean isFinished = ongoingMatchService.processMatchScore(uuid, winner);

        if (isFinished) {
            resp.sendRedirect(req.getContextPath() + "/matches");
        } else {
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
        }
    }
}
