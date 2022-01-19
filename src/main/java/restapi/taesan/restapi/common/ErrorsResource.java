package restapi.taesan.restapi.common;

import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;
import restapi.taesan.restapi.index.IndexController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors content) {
        super(content);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}