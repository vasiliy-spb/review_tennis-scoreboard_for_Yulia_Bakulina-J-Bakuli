package model;

public enum GamePoint {
    LOVE,
    FIFTEEN,
    THIRTY,
    FORTY,
    ADVANTAGE;

    public GamePoint nextInGame() {
        return switch (this) {
            case LOVE -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            case FORTY, ADVANTAGE -> throw new IllegalStateException("Game point cannot increment from " + this);
        };
    }

    public String display() {
        return switch (this) {
            case LOVE -> "0";
            case FIFTEEN -> "15";
            case THIRTY -> "30";
            case FORTY -> "40";
            case ADVANTAGE -> "AD";
        };
    }
}
