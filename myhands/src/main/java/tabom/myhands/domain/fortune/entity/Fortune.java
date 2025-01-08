package tabom.myhands.domain.fortune.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fortune")
public class Fortune {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="fortune_id", nullable = false)
    private Long fortuneId;

    @Column(nullable = false)
    private String content;
}
