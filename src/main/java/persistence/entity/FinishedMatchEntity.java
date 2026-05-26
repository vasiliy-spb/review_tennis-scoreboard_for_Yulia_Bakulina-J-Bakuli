package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "matches")
public class FinishedMatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private PlayerEntity player1;
    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private PlayerEntity player2;
    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = false) //Todo to check whether i need to delete some extra checks
    private PlayerEntity winner;
    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;
}
