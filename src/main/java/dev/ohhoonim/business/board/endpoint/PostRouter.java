package dev.ohhoonim.business.board.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.unit.Endpoint;

@Configuration
public class PostRouter implements Endpoint {

    @Bean
    RouterFunction<ServerResponse> router(PostHandler handler) {
        return RouterFunctions.route().path("/posts", builder -> 
            builder.GET("", handler::list)
                .GET("/user/{id}", handler::listByUser)
                .GET("/{id}", handler::post)
                .POST("", handler::addPost)
                .PUT("/{id}", handler::modifyPost)
                .DELETE("/{id}", handler::removePost)
        ).build();
    }
}