package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.repository.FabLabsIoRepository;
import lat.fab.app.resource.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class FabLabsIoRepositoryImpl implements FabLabsIoRepository {

    private final WebClient webClient;

    public FabLabsIoRepositoryImpl() {
        this.webClient = WebClient.create(Constants.FABLABS_IO_API_URL);
    }

    @Override
    public Mono<Long> count() {

        return webClient.get()
                .uri("/0/labs.json")
                .retrieve()
                .bodyToFlux(Object.class)
                .count();
    }
}
