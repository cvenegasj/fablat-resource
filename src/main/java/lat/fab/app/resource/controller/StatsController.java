package lat.fab.app.resource.controller;

import lat.fab.app.resource.repository.FabLabsIoRepository;
import lat.fab.app.resource.repository.FabberDAO;
import lat.fab.app.resource.repository.GroupDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final FabberDAO fabberDAO;
    private final GroupDAO groupDAO;
    private final FabLabsIoRepository fabLabsIoRepository;

    @GetMapping("/general")
    public Mono<Map<String, String>> getGeneralStatsLanding() {

        return fabLabsIoRepository.count()
                .map(labsCount -> Map.of(
                        "usersCount", String.valueOf(fabberDAO.count()),
                        "groupsCount", String.valueOf(groupDAO.count()),
                        "labsCount", String.valueOf(labsCount)));
    }
}