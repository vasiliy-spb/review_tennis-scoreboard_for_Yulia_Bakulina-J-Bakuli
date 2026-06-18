package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Check(constraints = "player1_id <> player2_id")
@Check(constraints = "winner_id = player1_id OR winner_id = player2_id")
@Table(name = "matches")
public class FinishedMatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "player1_id", nullable = false)
    private PlayerEntity player1;
    @ManyToOne(optional = false)
    @JoinColumn(name = "player2_id", nullable = false)
    private PlayerEntity player2;
    @ManyToOne(optional = false)
    @JoinColumn(name = "winner_id", nullable = false)
    private PlayerEntity winner;
    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;

    public FinishedMatchEntity(PlayerEntity player1, PlayerEntity player2,
                               PlayerEntity winner, LocalDateTime finishedAt) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.winner = Objects.requireNonNull(winner);
        this.finishedAt = Objects.requireNonNull(finishedAt);
    }
}
