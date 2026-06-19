package dao;

import exception.AlreadyExistsException;
import exception.NotFoundException;
import exception.ValidationException;
import model.Player;
import util.PlayerUtils;
import validation.PlayerValidation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPlayerDao implements PlayerDao {
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public Player save(Player player) {
        PlayerValidation.validatePlayerForCreate(player);
        String normalizedName = PlayerUtils.normalizeName(player.name());

        boolean alreadyExists = players.values().stream()
                .anyMatch(existingPlayer -> normalizedName.equals(existingPlayer.name()));

        if (alreadyExists) {
            throw new AlreadyExistsException("Player with name=" + normalizedName + " already exists.");
        }

        int id = nextId.getAndIncrement();

        Player savedPlayer = new Player(id, normalizedName);
        players.put(id, savedPlayer);
        return savedPlayer;
    }

    @Override
    public Player findByName(String name) {
        PlayerValidation.validatePlayerName(name);
        String normalizedName = PlayerUtils.normalizeName(name);
        return players.values().stream().filter(
                        player -> normalizedName.equals(player.name()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Player not found by name=" + name));
    }

    @Override
    public Player findById(Integer id) {
        PlayerValidation.validatePlayerId(id);
        Player player = players.get(id);
        if (player == null) {
            throw new NotFoundException("Player not found for id=" + id);
        }
        return player;
    }
}
