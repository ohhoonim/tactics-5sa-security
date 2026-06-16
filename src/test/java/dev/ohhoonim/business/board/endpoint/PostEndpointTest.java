package dev.ohhoonim.business.board.endpoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import dev.ohhoonim.business.board.application.PostRequest;
import dev.ohhoonim.business.board.application.PostResponse;
import dev.ohhoonim.business.board.application.PostService;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.Paged;
import dev.ohhoonim.component.model.paging.PagedData;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest
@Import({PostHandler.class, PostRouter.class})
public class PostEndpointTest {
    
    @Autowired MockMvcTester tester;
    @MockitoBean PostService service;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void list_test() {

        when(service.posts(any(), any()))
            .thenReturn(new PagedData<PostResponse>(List.of(), new Paged(1, 10, 0)));

        tester.get().uri("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .param("searchWord", "")
            .content(objectMapper.writeValueAsString(new PageRequest(1, 10)))
            .assertThat().apply(print()).hasStatusOk();
    }

    @Test
    void list_by_user_test(){
        when(service.postsByUser(any(), any()))
            .thenReturn(new PagedData<PostResponse>(List.of(), new Paged(1, 10, 0)));

        tester.get().uri("/posts/user/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PageRequest(1, 10)))
            .assertThat().apply(print()).hasStatusOk();
    }

    @Test
    void post_test() {
        PostId postId = PostId.Creator.generate();
        when(service.post(any()))
            .thenReturn(new PostResponse(postId, 
                "title1 ", "contents 1", Instant.now(), "nickname"));
        tester.get().uri("/posts/{id}", postId.getRawValue())
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat().hasStatusOk()
            .bodyJson().extractingPath("$.data.postId").isEqualTo(postId.getPublicValue());
    }

    @Test
    void add_post_test() {
        var newPost = new PostRequest("new title", "new contents", "some nick") ;
        var newPostId = PostId.Creator.generate();
        when(service.addPost(any())).thenReturn(newPostId);

        tester.post().uri("/posts").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(newPost))
            .assertThat().bodyJson().extractingPath("$.data").isEqualTo(newPostId.getPublicValue());

    }

    @Test
    void remove_post_test() {
        var postId = PostId.Creator.generate();

        tester.delete().uri("/posts/{id}", postId.getPublicValue())
            .contentType(MediaType.APPLICATION_JSON).exchange();

        verify(service, times(1)).removePost(eq(postId));
    }

}
