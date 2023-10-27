package lat.fab.app.resource.repository;

import reactor.core.publisher.Mono;

public interface FabLabsIoRepository {

    Mono<Long> count();
}
