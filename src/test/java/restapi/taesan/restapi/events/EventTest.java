package restapi.taesan.restapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;


public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("TaeSan")
                .description("TT EE SS TT")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        Event event = new Event();
        String name = "Event";

        // When
        event.setName(name);
        event.setDescription("Spring");

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo("Spring");
    }

    // @Parameters(method = "paramsForTestFree")
    @ParameterizedTest()
    @MethodSource("provideForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) throws Exception {
        //given
        Event event = Event.builder()
                    .basePrice(basePrice)
                    .maxPrice(maxPrice)
                    .build()
                ;
        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Object[] provideForTestFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    @ParameterizedTest()
    @MethodSource("provideForTestOffline")
    public void testOffline(String location, boolean isOffline) throws Exception {
        //given
        Event event = Event.builder()
                .location(location)
                .build()
        ;
        //when
        event.update();
        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Object[] provideForTestOffline() {
        return new Object[] {
                new Object[] {"카페", true},
                new Object[] {null, false},
                new Object[] {"    ", false},
        };
    }
}