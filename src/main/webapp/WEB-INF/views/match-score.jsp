<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Match Score</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body>
<header class="header">
    <section class="nav-header">
        <div class="brand">
            <div class="nav-toggle">
                <img src="${pageContext.request.contextPath}/images/menu.png" alt="Logo" class="logo">
            </div>
            <span class="logo-text">TennisScoreboard</span>
        </div>
        <div>
            <nav class="nav-links">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">Home</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Current match</h1>
            <c:if test="${not empty errorMessage}">
                <div class="error-box">
                    <span class="error-status">${errorStatus}</span>
                    <span class="error-message">${errorMessage}</span>
                </div>
            </c:if>
            <c:if test="${empty errorMessage}">
            <form method="post"
                  action="${pageContext.request.contextPath}/match-score"
                  onsubmit="this.querySelectorAll('button[type=submit]').forEach(btn => { btn.disabled = true;});">
                <input type="hidden" name="winner" id="winnerField" value="">
                <input type="hidden" name="uuid" value="${uuid}">
                <div class="current-match-image"></div>
                <section class="score">
                    <table class="table">
                        <thead class="result">
                        <tr>
                            <th class="table-text">Player</th>
                            <th class="table-text">Sets</th>
                            <th class="table-text">Games</th>
                            <th class="table-text">Points</th>
                            <th class="table-text">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="player1">
                            <td class="table-text">${player1Name}</td>
                            <td class="table-text">${matchState.player1Sets}</td>
                            <td class="table-text">${matchState.player1GamesInSet}</td>
                            <td class="table-text">${matchState.player1PointsDisplay}</td>
                            <td class="table-text">
                                <button class="score-btn"
                                        type="submit"
                                        onclick="document.getElementById('winnerField').value='player1';">
                                    Score
                                </button>
                            </td>
                        </tr>
                        <tr class="player2">
                            <td class="table-text">${player2Name}</td>
                            <td class="table-text">${matchState.player2Sets}</td>
                            <td class="table-text">${matchState.player2GamesInSet}</td>
                            <td class="table-text">${matchState.player2PointsDisplay}</td>
                            <td class="table-text">
                                <button class="score-btn"
                                        type="submit"
                                        onclick="document.getElementById('winnerField').value='player2';">
                                    Score
                                </button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </section>
            </form>
            </c:if>
    </div>
</main>
<footer>
    <div class="footer">
        <p>&copy; Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/">zhukovsd/java-backend-learning-course</a> roadmap.</p>
    </div>
</footer>
</body>
</html>
