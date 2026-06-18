package mapper;

import model.Player;
import persistence.entity.PlayerEntity;

public final class H2PlayerMapper {
    private H2PlayerMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Player toPlayer(PlayerEntity entity) {
        return new Player(entity.getId(), entity.getName());
    }

    public static PlayerEntity toEntity(String normalizedName) {
        return new PlayerEntity(normalizedName);
    }
}
