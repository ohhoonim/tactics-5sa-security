package dev.ohhoonim.business.board.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/posts")
public class PostController {
    
    @GetMapping("{id}")
    public String getMethodName(@PathVariable("id") String postId) {
        return "posts : " + postId;
    }
    
}
