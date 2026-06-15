package dev.ohhoonim.business.board.activity;

import dev.ohhoonim.business.board.model.Post;
import dev.ohhoonim.business.board.model.PostId;

public interface PostCommandActivity {

    PostId addPost(Post newPost);

    void modifyPost(Post post);

    void removePost(PostId postId);
}
