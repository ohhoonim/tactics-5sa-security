package dev.ohhoonim.business.board.infra.activity;

import dev.ohhoonim.business.board.activity.PostCommandActivity;
import dev.ohhoonim.business.board.model.Post;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.component.model.unit.Activity;

@Activity
public class PostCommandActions implements PostCommandActivity{

    @Override
    public PostId addPost(Post newPost) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPost'");
    }

    @Override
    public void modifyPost(Post post) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'modifyPost'");
    }

    @Override
    public void removePost(PostId postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removePost'");
    }
    
}
