package dev.ohhoonim.business.board.activity;

import java.util.Optional;
import dev.ohhoonim.business.board.application.SearchCondition;
import dev.ohhoonim.business.board.model.Post;
import dev.ohhoonim.business.board.model.PostComponent.User;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.PagedData;

public interface PostQueryActivity {

    PagedData<Post> searchPost(SearchCondition searchCondition, PageRequest pageRequest);

    Optional<Post> detailPost(PostId postId); 

    PagedData<Post> searchPostByUser(User user, PageRequest pageRequest);
    
}
