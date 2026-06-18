package filter;

import exception.ExceptionHandler;
import exception.ExceptionMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "filter2")
public class ExceptionHandlingFilter extends HttpFilter {
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            if (res.isCommitted()) {
                throw e;
            }
            writeErrorResponse(req, res, e);
        }
    }

    private void writeErrorResponse(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException, ServletException {
        ExceptionMessage mapped = exceptionHandler.mapToMessage(e);
        int status = mapped.getStatus();
        String message = exceptionHandler.resolveClientMessage(e, mapped);

        String jsp = null;

        switch (req.getServletPath()) {
            case "/home": {
                jsp = "/WEB-INF/views/home.jsp";
                break;
            }
            case "/new-match": {
                String player1Name = req.getParameter("player1Name");
                String player2Name = req.getParameter("player2Name");
                req.setAttribute("player1Name", player1Name);
                req.setAttribute("player2Name", player2Name);
                jsp = "/WEB-INF/views/new-match.jsp";
                break;
            }
            case "/match-score": {
                String uuid = req.getParameter("uuid");
                req.setAttribute("uuid", uuid);
                jsp = "/WEB-INF/views/match-score.jsp";
                break;
            }
            case "/matches": {
                String page = req.getParameter("page");
                String filterByPlayerName = req.getParameter("filter_by_player_name");
                req.setAttribute("page", page);
                req.setAttribute("filterByPlayerName", filterByPlayerName);
                jsp = "/WEB-INF/views/matches.jsp";
                break;
            }
        }
        req.setAttribute("errorMessage", message);

        if (jsp == null) {
            throw new ServletException(e);
        }
        res.setStatus(status);
        req.setAttribute("errorStatus", status);
        req.getRequestDispatcher(jsp).forward(req, res);
    }
}
