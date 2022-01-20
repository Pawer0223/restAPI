package restapi.taesan.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import restapi.taesan.restapi.accounts.Account;
import restapi.taesan.restapi.accounts.AccountAdapter;
import restapi.taesan.restapi.accounts.CurrentUser;
import restapi.taesan.restapi.common.ErrorsResource;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return getErrorsResourceResponseEntity(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return getErrorsResourceResponseEntity(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        // 링크를 추가, 현재 self 링크는 Resouce에 붙였는데, 나머지도 Resource에 붙여도 나쁘지않음. 아직 강의에선 냅둬서 나도 냅둠.
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var entityModels = assembler.toModel(page, e -> new EventResource(e));
        entityModels.add(Link.of("/docs/index.html#resources-event-list").withRel("profile"));
        if (account != null) {
            entityModels.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {

        if (errors.hasErrors())
            return getErrorsResourceResponseEntity(errors);

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty())
            return ResponseEntity.notFound().build();

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors())
            return getErrorsResourceResponseEntity(errors);

        Event event = optionalEvent.get();
        if (!event.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(eventDto, event); // eventDto 의 값을 -> event로 복사.
        Event updateEvent = this.eventRepository.save(event);

        EventResource eventResource = new EventResource(updateEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity<ErrorsResource> getErrorsResourceResponseEntity(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
