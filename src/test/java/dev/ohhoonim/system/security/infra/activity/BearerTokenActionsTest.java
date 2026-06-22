package dev.ohhoonim.system.security.infra.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.ohhoonim.system.security.activity.out.BearerTokenPort;
import dev.ohhoonim.system.security.model.JwtToken;
import dev.ohhoonim.system.security.model.JwtTransitionEventPolicy;
import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class BearerTokenActionsTest {

    @Mock
    private BearerTokenPort tokenPort;

    @Spy
    private JwtTransitionEventPolicy policy;

    @InjectMocks
    private BearerTokenActions bearerTokenActions;

    @Test
    @DisplayName("토큰 검증이 성공하면 주체 정보를 기반으로 JwtToken 객체를 구성하고 상태 전이를 수행한다")
    void verifyWith_Success() {
        String token = "valid.jwt.token";
        String expectedSubject = UUID.randomUUID().toString();
        Claims mockClaims = Mockito.mock(Claims.class);
        
        given(mockClaims.getSubject()).willReturn(expectedSubject);
        given(tokenPort.getClaims(anyString())).willReturn(mockClaims);

        JwtToken result = bearerTokenActions.verifyWith(token);

        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        verify(tokenPort).getClaims(token);
    }
}