package dev.ohhoonim.business.board.endpoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.security.Provider;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import dev.ohhoonim.business.board.application.PostRequest;
import dev.ohhoonim.business.board.application.PostResponse;
import dev.ohhoonim.business.board.application.PostService;
import dev.ohhoonim.business.board.model.PostId;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.paging.Paged;
import dev.ohhoonim.component.model.paging.PagedData;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest
@Import({PostHandler.class, PostRouter.class, SecurityConfig.class})
public class PostEndpointTest {

    @Autowired
    MockMvcTester tester;
    @MockitoBean
    PostService service;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void list_test() {

        when(service.posts(any(), any()))
                .thenReturn(new PagedData<PostResponse>(List.of(), new Paged(1, 10, 0)));

        tester.get().uri("/posts").contentType(MediaType.APPLICATION_JSON).param("searchWord", "")
                .content(objectMapper.writeValueAsString(new PageRequest(1, 10))).assertThat()
                .apply(print()).hasStatusOk();
    }

    @Test
    void list_by_user_test() {
        when(service.postsByUser(any(), any()))
                .thenReturn(new PagedData<PostResponse>(List.of(), new Paged(1, 10, 0)));

        tester.get().uri("/posts/user/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PageRequest(1, 10))).assertThat()
                .apply(print()).hasStatusOk();
    }

    @Test
    void post_test() {
        PostId postId = PostId.Creator.generate();
        when(service.post(any())).thenReturn(
                new PostResponse(postId, "title1 ", "contents 1", Instant.now(), "nickname"));
        tester.get().uri("/posts/{id}", postId.getRawValue())
                .contentType(MediaType.APPLICATION_JSON).assertThat().hasStatusOk().bodyJson()
                .extractingPath("$.data.postId").isEqualTo(postId.getPublicValue());
    }

    @Test
    void add_post_test() {
        var newPost = new PostRequest("new title", "new contents", "some nick");
        var newPostId = PostId.Creator.generate();
        when(service.addPost(any())).thenReturn(newPostId);

        tester.post().uri("/posts").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newPost)).assertThat().bodyJson()
                .extractingPath("$.data").isEqualTo(newPostId.getPublicValue());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void remove_post_test() {
        var postId = PostId.Creator.generate();

        tester.delete().uri("/posts/{id}", postId.getPublicValue())
                .contentType(MediaType.APPLICATION_JSON).exchange();

        verify(service, times(1)).removePost(eq(postId));
    }
}


@TestConfiguration
@EnableWebSecurity(debug = true)
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilter(HttpSecurity http) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                    .anyRequest().permitAll())
        ;
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000"); // 허용할 프론트엔드 도메인
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용 (GET, POST 등)
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 및 인증 정보 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}
