package dev.ohhoonim.business.board.endpoint;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.payload.DefaultEndpointHandler;
import dev.ohhoonim.component.model.unit.Endpoint;

@Configuration
public class UserRouter implements Endpoint {

    @Bean
    RouterFunction<ServerResponse> userRoute(UserHandler handler) {
        return route()
                .path("/users",
                        builder -> builder.GET("/{id}", handler::getUser)
                                .GET("/json/{id}", handler::getJsonUser)
                                .GET("/exception/throw", handler::throwException))
                .filter(new DefaultEndpointHandler()).build();

    }

    // HandlerFilterFunction<ServerResponse, ServerResponse> filterFunction = (request, next) -> {
    //     try {
    //         ServerResponse response = next.handle(request);
    //         if (response instanceof EntityResponse<?> entityResponse) {
    //             Object body = entityResponse.entity();
    //             if (body instanceof Response || body instanceof Resource) {
    //                 return response;
    //             }
    //             return ServerResponse.from(response).contentType(MediaType.APPLICATION_JSON)
    //                     .body(new Response.Success<>(ResponseCode.SUCCESS, body));
    //         }
    //         return response;
    //     } catch (Exception e) {
    //         return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
    //                 .body(new Response.Fail<>(ResponseCode.ERROR, e.getMessage(), null));
    //     }
    // };
}
