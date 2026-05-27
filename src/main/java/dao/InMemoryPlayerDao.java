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
        if (player == null) {
            throw new ValidationException("Player must not be null.");
        }

        String rawName = player.getName();
        PlayerValidation.validatePlayerName(rawName);
        String normalizedName = PlayerUtils.normalizeName(rawName);

        boolean alreadyExists = players.values().stream()
                .anyMatch(existingPlayer -> normalizedName.equals(existingPlayer.getName()));

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
                        player -> normalizedName.equals(player.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Player not found by name=" + name));
    }

    @Override
    public Player findById(Integer id) {
        if (id == null) {
            throw new ValidationException("Player's id must not be null.");
        }
        Player player = players.get(id);
        if (player == null) {
            throw new NotFoundException("Player not found for id=" + id);
        }
        return player;
    }
}
