package dev.ohhoonim.system.security.endpoint;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import dev.ohhoonim.component.model.payload.Response;
import dev.ohhoonim.component.model.payload.ResponseCode;
import dev.ohhoonim.component.model.unit.Endpoint;
import dev.ohhoonim.system.security.model.SecurityAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

public class JwtExceptionFilter extends OncePerRequestFilter implements Endpoint{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityAuthenticationException e) {
            log.error("security filter exception: {}", e.getMessage());
            var errorResponse = new Response.Fail<String>(ResponseCode.ERROR, e.errorCode(), e.getMessage(), "");

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new JsonMapper().writeValueAsString(errorResponse));
        }
    }
    
}
