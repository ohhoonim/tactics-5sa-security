package dev.ohhoonim.business.board.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(PostController.class)
class PostControllerTest {
    
    @Autowired
    MockMvcTester mockMvc;

    @Test
    void getPost() {
        mockMvc.get().uri("/posts/{id}", "PRD-A11D2")
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat().bodyText().isEqualTo("posts : PRD-A11D2");
    }
}
