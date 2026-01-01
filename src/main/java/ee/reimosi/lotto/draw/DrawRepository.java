package ee.reimosi.lotto.draw;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    boolean existsByDrawId(String drawId);
}
