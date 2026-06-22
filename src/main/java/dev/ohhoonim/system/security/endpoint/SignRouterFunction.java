package dev.ohhoonim.system.security.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.payload.DefaultEndpointHandler;
import dev.ohhoonim.component.model.unit.Endpoint;

@Configuration
public class SignRouterFunction implements Endpoint {
    
    @Bean 
    RouterFunction<ServerResponse> signRouter (SignHandler handler) {
        return RouterFunctions.route()
        .path("/sign", builder -> builder
            .POST("/in", request -> handler.signIn(request))
            .POST("/refresh", request -> handler.refresh(request))
        ).filter(new DefaultEndpointHandler())
        .build() ;

    }
}
