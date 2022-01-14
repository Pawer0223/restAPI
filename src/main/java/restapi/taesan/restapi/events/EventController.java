package restapi.taesan.restapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class EventController {

    @PostMapping("/api/events")
    public ResponseEntity createEvent() {

        URI createdUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        System.out.println("createdUri: " + createdUri);
        return ResponseEntity.created(createdUri).build();
    }
}
