package dev.ohhoonim.business.board.application;

import java.time.Instant;
import org.springframework.stereotype.Service;
import dev.ohhoonim.business.board.activity.PostCommandActivity;
import dev.ohhoonim.business.board.activity.PostQueryActivity;
import dev.ohhoonim.business.board.model.BoardException;
import dev.ohhoonim.business.board.model.Post;
import dev.ohhoonim.business.board.model.PostComponent.User;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.business.board.model.UserId;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.PagedData;

@Service
public class PostService {

    private final PostQueryActivity postQueryActivity;
    private final PostCommandActivity postCommandActivity;

    public PostService(PostQueryActivity postQueryActivity,
            PostCommandActivity postCommandActivity) {
        this.postQueryActivity = postQueryActivity;
        this.postCommandActivity = postCommandActivity;
    }

    // 공통사항: pageRequest는 body로 받음

    // GET /posts?q=  
    public PagedData<PostResponse> posts(SearchCondition searchCondition, PageRequest pageRequest) {
        PagedData<Post> pagedPost = postQueryActivity.searchPost(searchCondition, pageRequest);
        var responsePosts = pagedPost.contents().stream().map(p -> new PostResponse(p.postId(),
                p.title(), p.contents(), p.lastModifiedAt(), p.user().nickName())).toList();

        return new PagedData<PostResponse>(responsePosts, pagedPost.paged());
    }

    // GET /posts/user/{id}
    public PagedData<PostResponse> postsByUser(UserId userId, PageRequest pageRequest) {
        User user = new User(userId, null);
        PagedData<Post> pagedPost = postQueryActivity.searchPostByUser(user, pageRequest);
        var responsePosts = pagedPost.contents().stream().map(p -> new PostResponse(p.postId(),
                p.title(), p.contents(), p.lastModifiedAt(), p.user().nickName())).toList();

        return new PagedData<PostResponse>(responsePosts, pagedPost.paged());
    }

    // GET /posts/{id}
    public PostResponse post(PostId postId) {
        Post post = postQueryActivity.detailPost(postId)
                .orElseThrow(() -> new BoardException("post가 존재하지 않습니다."));
        return new PostResponse(postId, post.title(), post.contents(), post.lastModifiedAt(),
                post.user().nickName());
    }

    // post 작성 후 상세 페이지로 이동
    // POST /posts
    public PostResponse addPost(PostRequest postRequest) {
        var newPost = new Post(PostId.Creator.generate(), postRequest.title(),
                postRequest.contents(), null, null);
        PostId postId = postCommandActivity.addPost(newPost);
        return this.post(postId);
    }

    public PostResponse modifyPost(PostId postId, PostRequest postRequest, String operatorId) {
        var post = new Post(postId, postRequest.title(), postRequest.contents(), Instant.now(),
                new User(UserId.Creator.fromPublic(operatorId), ""));
        postCommandActivity.modifyPost(post);
        return this.post(postId); // 필터링을 해야하므로 다시 조회
    }

    public PagedData<PostResponse> removePost(PostId postId) {
        postCommandActivity.removePost(postId);
        return this.posts(SearchCondition.all(), new PageRequest(1, 10));
    }
}
