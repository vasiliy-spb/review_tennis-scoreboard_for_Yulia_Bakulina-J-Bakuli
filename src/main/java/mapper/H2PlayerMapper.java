package mapper;

import exception.ValidationException;
import model.Player;
import persistence.entity.PlayerEntity;
import validation.PlayerValidation;

public final class H2PlayerMapper {
    private H2PlayerMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Player toPlayer(PlayerEntity entity) {
        return new Player(entity.getId(), entity.getName());
    }

    public static PlayerEntity toEntity(String normalizedName) {
        PlayerValidation.validatePlayerName(normalizedName);

        PlayerEntity entity = new PlayerEntity();
        entity.setName(normalizedName);
        return entity;
    }

    public static PlayerEntity toEntityById(Integer playerId) {
        if (playerId == null) {
            throw new ValidationException("playerId cannot be null");
        }
        PlayerEntity entity = new PlayerEntity();
        entity.setId(playerId);
        return entity;
    }
}
