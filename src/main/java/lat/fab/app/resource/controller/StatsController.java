package lat.fab.app.resource.controller;

import lat.fab.app.resource.service.UserStatsService;
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

    private final UserStatsService userStatsService;

    @GetMapping("/general")
    public Mono<Map<String, String>> getGeneralStatsLanding() {
        return userStatsService.getGeneralStatsLanding();
    }


}
