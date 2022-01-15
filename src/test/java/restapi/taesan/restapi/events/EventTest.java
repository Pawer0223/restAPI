package restapi.taesan.restapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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

    @Test
    // @Parameters(method = "paramsForTestFree")
    @Parameters()
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

    private Object[] parametersForTestFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    @Test
    @Parameters()
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

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"카페", true},
                new Object[] {null, false},
                new Object[] {"    ", false},
        };
    }
}