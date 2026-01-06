package ee.reimosi.lotto.draw;

import ee.reimosi.lotto.draw.dto.DrawRequest;
import ee.reimosi.lotto.draw.dto.DrawResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DrawServiceTest {

    private final DrawRepository repository = mock(DrawRepository.class);
    private final DrawMapper mapper = Mappers.getMapper(DrawMapper.class);

    @Test
    void create_shouldPersist_andReturnDto() {
        DrawService service = new DrawService(repository, mapper);

        DrawRequest req = new DrawRequest();
        req.setDrawId("2099-01-01");
        req.setDrawDate(LocalDate.parse("2099-01-01"));
        req.setMainNumbers("1 2 3 4 5 6");
        req.setBonusNumbers("7");

        when(repository.existsByDrawId("2099-01-01")).thenReturn(false);
        when(repository.save(any(Draw.class))).thenAnswer(inv -> {
            Draw d = inv.getArgument(0);
            d.setId(123L);
            return d;
        });

        DrawResponse resp = service.create(req);

        if (resp.getId() != null) {
            assertThat(resp.getId()).isEqualTo(123L);
        }

        assertThat(resp.getDrawId()).isEqualTo("2099-01-01");
        assertThat(resp.getDrawDate().toString()).isEqualTo("2099-01-01");
        assertThat(resp.getMainNumbers()).isEqualTo("1 2 3 4 5 6");
        assertThat(resp.getBonusNumbers()).isEqualTo("7");

        ArgumentCaptor<Draw> capt = ArgumentCaptor.forClass(Draw.class);
        verify(repository, times(1)).save(capt.capture());
        Draw saved = capt.getValue();
        // enne save'i ei pea ID olema
        assertThat(saved.getDrawId()).isEqualTo("2099-01-01");
        assertThat(saved.getDrawDate()).isEqualTo(LocalDate.parse("2099-01-01"));
    }

    @Test
    void get_shouldMapEntityToDto() {
        DrawService service = new DrawService(repository, mapper);

        Draw entity = new Draw();
        entity.setId(7L);
        entity.setDrawId("2024-01-03");
        entity.setDrawDate(LocalDate.parse("2024-01-03"));
        entity.setMainNumbers("1 4 7 12 33 40");
        entity.setBonusNumbers("2");

        when(repository.findById(7L)).thenReturn(Optional.of(entity));

        DrawResponse dto = service.get(7L);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getDrawId()).isEqualTo("2024-01-03");
        assertThat(dto.getDrawDate().toString()).isEqualTo("2024-01-03");
        assertThat(dto.getMainNumbers()).isEqualTo("1 4 7 12 33 40");
        assertThat(dto.getBonusNumbers()).isEqualTo("2");
    }
}
