package dev.ohhoonim.business.board.endpoint;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest({UserHandler.class, UserRouter.class})
public class UserContollerTest {

    @Autowired
    MockMvcTester mockMvc;
    
    @Test
    void getUser() {
        mockMvc.get().uri("/users/{id}", "123")
            .assertThat().hasStatusOk()
            .bodyJson().extractingPath("$.data").isEqualTo("User ID: 123");
    }

    @Test
    void getJsonUser() {
        var json = mockMvc.get().uri("/users/json/{id}", "123")
            .accept(MediaType.APPLICATION_JSON)
            .assertThat().hasStatusOk()
            .bodyJson();

        assertAll(
            () -> json.extractingPath("$.data.name").isEqualTo("matthew"),
            () -> json.extractingPath("$.data.age").isEqualTo(12),
            () -> json.extractingPath("$.data.id").isEqualTo("123")
        );
            
    }

    @Test
    void throwException() {
        mockMvc.get().uri("/users/exception/throw")
            .accept(MediaType.APPLICATION_JSON)
            .assertThat().hasStatusOk().apply(print())
            .bodyJson().extractingPath("$.code").isEqualTo("ERROR");
    }
}
