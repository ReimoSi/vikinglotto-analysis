package ee.reimosi.lotto.draw;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "draw", indexes = {
        @Index(name = "ux_draw_draw_id", columnList = "drawId", unique = true)
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Draw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "draw_id", nullable = false, unique = true, length = 32)
    private String drawId;

    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    @Column(name = "main_numbers", nullable = false, length = 64)
    private String mainNumbers;

    @Column(name = "bonus_numbers", nullable = false, length = 32)
    private String bonusNumbers;
}
