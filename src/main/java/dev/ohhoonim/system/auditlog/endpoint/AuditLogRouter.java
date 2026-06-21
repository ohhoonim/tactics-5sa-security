package dev.ohhoonim.system.auditlog.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.payload.DefaultEndpointHandler;
import dev.ohhoonim.component.model.unit.Endpoint;

@Configuration
public class AuditLogRouter implements Endpoint {

    @Bean
    public RouterFunction<ServerResponse> auditLogRoute(AuditLogHandler handler) {
        return RouterFunctions.route()
                .path("/auditlog", builder -> builder
                        .GET("/targets/{targetType}", handler::getLogsByTarget)
                        .GET("/actors/{actorId}", handler::getLogsByActor)
                        .GET("/{id}", handler::getDetail))
                .filter(new DefaultEndpointHandler())
                .build();
    }
}
