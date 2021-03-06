package restapi.taesan.restapi.events;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import restapi.taesan.restapi.accounts.Account;
import restapi.taesan.restapi.accounts.AccountRepository;
import restapi.taesan.restapi.accounts.AccountRole;
import restapi.taesan.restapi.accounts.AccountService;
import restapi.taesan.restapi.common.AppProperties;
import restapi.taesan.restapi.common.BaseControllerTest;
import restapi.taesan.restapi.common.TestDescription;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {

    @Autowired EventRepository eventRepository;

    @Autowired AccountRepository accountRepository;

    @Autowired AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("??????????????? ???????????? ???????????? ?????????")
    public void createEvent() throws Exception {

        // given
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
                .location("?????? ???")
                .build();

        // when
        mockMvc.perform(post("/api/events/")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                // then
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
                        relaxedResponseFields(
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
    @TestDescription("?????? ?????? ??? ?????? ?????? ???????????? ??????")
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
                .location("?????? ???")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("?????? ?????? ???????????? ??????")
    public void createEvent_bad_request_empty_input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("????????? ????????? ?????? ??????")
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
                .location("?????? ???")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
    @TestDescription("????????? ???, 30?????? ???????????? 10?????? ????????? ????????? ????????????")
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
    @TestDescription("????????? ???, 30?????? ???????????? 10?????? ????????? ????????? ????????????")
    public void queryEventsWithAuthentication() throws Exception {
        // given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        // when
        this.mockMvc.perform(get("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("????????? ???????????? ?????? ????????????")
    public void getEvent() throws Exception {
        // given
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);
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
    @TestDescription("????????? ?????? ?????? 404")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/161718"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("???????????? Event ??????")
    public void updateEvent() throws Exception {

        Account account = this.createAccount();
        Event event = generateEvent(42, account);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
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
    @TestDescription("???????????? ???????????? ?????? ??????")
    public void updateEvent400_empty() throws Exception {

        Event event = new Event();
        event = this.eventRepository.save(event);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                 )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("???????????? ????????? ????????? ?????? ??????")
    public void updateEvent400_wrong() throws Exception {

        Event event = generateEvent(1);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto))
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("?????? ???????????? ??????????????? ?????? ??????")
    public void updateEvent404() throws Exception {

        Event event = generateEvent(1);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/67")
                            .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i, Account account) {
        Event event = buildEvent(i);
        event.setManager(account);
        return eventRepository.save(event);
    }

    private Event generateEvent(int i) {
        return eventRepository.save(buildEvent(i));
    }

    private Event buildEvent(int i) {
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
                    .location("?????? ???")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();

        return this.eventRepository.save(event);
    }

    private String  getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        if (needToCreateAccount)
            createAccount();

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        String responseBody = response.getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        Account taesan = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(taesan);
    }
}
