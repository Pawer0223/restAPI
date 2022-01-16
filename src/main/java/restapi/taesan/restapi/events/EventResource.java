package restapi.taesan.restapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

//public class EventResource extends RepresentationModel {
//
//    /**
//     * JsonUnwrapped 를 붙이지 않는다면 {"event": { ... }, "_links": { ... } } 로 반환
//     * 붙인다면 event의 필드가 모두 바깥으로 꺼내지고 { "field1": , "field2": , ... "_links": {...}} 로 반
//     */
//    @JsonUnwrapped
//    private Event event;
//
//    public EventResource(Event event) {
//        this.event = event;
//    }
//
//    public Event getEvent() {
//        return event;
//    }
//}

public class EventResource extends EntityModel<Event> { // 얘는 기본으로 JsonUnwrapped 적용되어 있음

    public EventResource(Event event) {
        super(event);
        // new Link("http://localhost:8080/api/events/3");
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
