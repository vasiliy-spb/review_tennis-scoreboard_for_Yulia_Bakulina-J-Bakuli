package dao;

import exception.NotFoundException;
import exception.ValidationException;
import model.OngoingMatch;
import validation.MatchValidation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOngoingMatchDao implements OngoingMatchDao {
    private final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();

    @Override
    public OngoingMatch save(OngoingMatch match) {
        MatchValidation.validateOngoingMatch(match);
        ongoingMatches.put(match.getUuid(), match);
        return match;
    }

    @Override
    public OngoingMatch findByUuid(UUID uuid) {
        if (uuid == null) {
            throw new ValidationException("Ongoing match uuid must not be null.");
        }
        OngoingMatch match = ongoingMatches.get(uuid);
        if (match == null) {
            throw new NotFoundException("Ongoing match not found for uuid=" + uuid);
        }
        return match;
    }

    @Override
    public void removeByUuid(UUID uuid) {
        if (uuid == null) {
            throw new ValidationException("Ongoing match uuid must not be null.");
        }
        OngoingMatch removed = ongoingMatches.remove(uuid);
        if (removed == null) {
            throw new NotFoundException("Ongoing match not found for uuid=" + uuid);
        }
    }
}
