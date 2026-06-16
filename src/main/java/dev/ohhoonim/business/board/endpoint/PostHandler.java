package dev.ohhoonim.business.board.endpoint;

import java.io.IOException;
import java.security.Principal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.business.board.application.PostRequest;
import dev.ohhoonim.business.board.application.PostService;
import dev.ohhoonim.business.board.application.SearchCondition;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.business.board.model.UserId;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.unit.Endpoint;
import jakarta.servlet.ServletException;

@Component
public class PostHandler implements Endpoint{

    private final PostService postService;

    public PostHandler (PostService postService) {
        this.postService = postService;
    }
    
    public ServerResponse list(ServerRequest request) throws ServletException, IOException {
        var searchCondition = new SearchCondition(request.param("q").orElse(""));
        var pageRequest = request.body(PageRequest.class);
        var posts = postService.posts(searchCondition, pageRequest);
        return ServerResponse.ok().body(posts);
    }

    public ServerResponse listByUser(ServerRequest request) throws ServletException, IOException {
        var userId = UserId.Creator.fromPublic(request.pathVariable("id"));
        var pageRequest = request.body(PageRequest.class);
        var posts = postService.postsByUser(userId, pageRequest);
        return ServerResponse.ok().body(posts);
    }

    public ServerResponse post(ServerRequest request) {
        PostId postId = PostId.Creator.fromPublic(request.pathVariable("id"));
        var post = postService.post(postId);
        return ServerResponse.ok().body(post);
    }

    public ServerResponse addPost(ServerRequest request) throws ServletException, IOException {
        var postRequest = request.body(PostRequest.class);
        var newPost = postService.addPost(postRequest);
        return ServerResponse.ok().body(newPost);
    }

    public ServerResponse modifyPost(ServerRequest request) throws ServletException, IOException {
        var postId = PostId.Creator.fromPublic(request.pathVariable("id"));
        var postRequest = request.body(PostRequest.class);
        // var operator = request.principal();
        var operator = UserId.Creator.generate();
        var newPost = postService.modifyPost(postId, postRequest, operator.getPublicValue());
        return ServerResponse.ok().body(newPost);
    }
    public ServerResponse removePost(ServerRequest request) throws ServletException, IOException {
        var postId = PostId.Creator.fromPublic(request.pathVariable("id"));
        var posts = postService.removePost(postId);
        return ServerResponse.ok().body(posts);
    }
}
