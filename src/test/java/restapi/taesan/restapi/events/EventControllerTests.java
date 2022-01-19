package restapi.taesan.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import restapi.taesan.restapi.common.BaseControllerTest;
import restapi.taesan.restapi.common.RestDocsConfiguration;
import restapi.taesan.restapi.common.TestDescription;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {

    @Autowired EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 1, 15, 3, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 1, 16, 3, 0))
                .beginEventDateTime(LocalDateTime.of(2021, 1, 17, 3, 0))
                .endEventDateTime(LocalDateTime.of(2021, 1, 18, 3, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("니네 집")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_link.profile").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query-events"),
                                fieldWithPath("_links.update-event.href").description("link to update-event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                                )
                ))
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용하는 경우")
    public void createEvent_bad_request() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 1, 15, 3, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 1, 16, 3, 0))
                .beginEventDateTime(LocalDateTime.of(2021, 1, 17, 3, 0))
                .endEventDateTime(LocalDateTime.of(2021, 1, 18, 3, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("니네 집")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우")
    public void createEvent_bad_request_empty_input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("잘못된 입력을 받는 경우")
    public void createEvent_bad_request_wrong_input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 1, 18, 3, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 1, 17, 3, 0))
                .beginEventDateTime(LocalDateTime.of(2021, 1, 16, 3, 0))
                .endEventDateTime(LocalDateTime.of(2021, 1, 15, 3, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("니네 집")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        // when
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
                )
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // given
        Event event = this.generateEvent(100);
        // when
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("이벤트 없을 때는 404")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/161718"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("존재하는 Event 수정")
    public void updateEvent() throws Exception {

        Event event = generateEvent(42);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("update-event"))
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우 실패")
    public void updateEvent400_empty() throws Exception {

        Event event = new Event();
        event = this.eventRepository.save(event);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                 )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 수정 실패")
    public void updateEvent400_wrong() throws Exception {

        Event event = generateEvent(1);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                    )
                        .andDo(print())
                        .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("없는 이벤트를 수정하려는 경우 실패")
    public void updateEvent404() throws Exception {

        Event event = generateEvent(1);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/67")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                )
                    .andDo(print())
                    .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                    .name("event " + i)
                    .description("description")
                    .beginEnrollmentDateTime(LocalDateTime.of(2021, 1, 15, 3, 0))
                    .closeEnrollmentDateTime(LocalDateTime.of(2021, 1, 16, 3, 0))
                    .beginEventDateTime(LocalDateTime.of(2021, 1, 17, 3, 0))
                    .endEventDateTime(LocalDateTime.of(2021, 1, 18, 3, 0))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("니네 집")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();

        return this.eventRepository.save(event);
    }
}
