package lat.fab.app.resource.repository.impl;

import lat.fab.app.resource.repository.FabLabsIoRepository;
import lat.fab.app.resource.util.Constants;
import lat.fab.app.resource.util.fablabs.Fablab;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
@Slf4j
public class FabLabsIoRepositoryImpl implements FabLabsIoRepository {

    private final WebClient webClient;
    private final Set<String> countriesCCA2CodesLatam = Set.of("AR", "AW", "BO", "BR", "CL", "CO", "CR",
            "CU", "EC", "SV", "GT", "GF", "HN", "JM", "MX", "NI", "PA", "PY", "PE", "PR", "SR", "TT",
            "UY", "VE");

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

    @Override
    public Mono<Long> countLabsLatam() {

        return webClient.get()
                .uri("/0/labs.json")
                .retrieve()
                .bodyToFlux(Fablab.class)
                .filter(fablab -> countriesCCA2CodesLatam.contains(fablab.getCountryCode()))
                .count();
    }
}
