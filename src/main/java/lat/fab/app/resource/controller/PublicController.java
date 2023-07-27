package lat.fab.app.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    private static int pingCounter = 0;

    @GetMapping("/secure")
    public String greet() {
        return String.format("Hello %s!", ++pingCounter);
    }

    @GetMapping("/healthcheck")
    public String healthCheck() {
        return String.format("Hello %s!", ++pingCounter);
    }

}
