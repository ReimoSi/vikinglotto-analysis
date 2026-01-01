package ee.reimosi.lotto.draw;

import ee.reimosi.lotto.draw.dto.DrawRequest;
import ee.reimosi.lotto.draw.dto.DrawResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository repo;
    private final DrawMapper mapper;

    public List<DrawResponse> list() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public DrawResponse get(Long id) {
        var e = repo.findById(id).orElseThrow(() -> new DrawsNotFound(id));
        return mapper.toResponse(e);
    }

    public DrawResponse create(DrawRequest r) {
        if (repo.existsByDrawId(r.getDrawId())) throw new DuplicateDrawId(r.getDrawId());
        var e = mapper.toEntity(r);
        e = repo.save(e);
        return mapper.toResponse(e);
    }

    public DrawResponse update(Long id, DrawRequest r) {
        var e = repo.findById(id).orElseThrow(() -> new DrawsNotFound(id));
        if (!e.getDrawId().equals(r.getDrawId()) && repo.existsByDrawId(r.getDrawId()))
            throw new DuplicateDrawId(r.getDrawId());
        mapper.updateEntityFromDto(r, e);
        e = repo.save(e);
        return mapper.toResponse(e);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new DrawsNotFound(id);
        repo.deleteById(id);
    }

    public static class DrawsNotFound extends RuntimeException {
        public DrawsNotFound(Long id) { super("Draw not found: " + id); }
    }
    public static class DuplicateDrawId extends RuntimeException {
        public DuplicateDrawId(String drawId) { super("Duplicate drawId: " + drawId); }
    }
}
