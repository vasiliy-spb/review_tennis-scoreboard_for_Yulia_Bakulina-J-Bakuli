package dto;

public record MatchScoreDto(
    String uuid,
    String player1Name,
    String player2Name,
    int player1Sets,
    int player2Sets,
    int player1GamesInSet,
    int player2GamesInSet,
    String player1PointsDisplay,
    String player2PointsDisplay) {
}
