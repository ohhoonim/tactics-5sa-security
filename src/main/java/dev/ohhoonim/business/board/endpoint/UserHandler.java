package dev.ohhoonim.business.board.endpoint;

import static org.springframework.web.servlet.function.ServerResponse.ok;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.business.board.model.PostComponent.User;
import dev.ohhoonim.business.board.model.UserId;
import dev.ohhoonim.component.model.unit.Endpoint;

/*
 * 이 방식을 채택하려는 이유 - 컨텍스트 스위칭 감소: 특정 기능을 수정할 때 IDE 탭을 여러 개 열 필요 없이 이 파일 하나만 보면 끝난다. - 도메인 중심 응집성:
 * User와 관련된 요청 방식이 한곳에 모여 있어 코드 가시 응집성이 높다. - 패키지 가시성 활용: UserHandler와 UserRouter를 public이 아닌
 * package-private(기본값)으로 두어 외부 노출을 최소화한다.
 */

@Component
public class UserHandler implements Endpoint {
    public ServerResponse getUser(ServerRequest request) {
        String id = request.pathVariable("id");
        String userInfo = "User ID: " + id;
        return ok().body(userInfo);
    }

    ServerResponse getJsonUser(ServerRequest request) {
        String id = request.pathVariable("id");
        return ok().contentType(MediaType.APPLICATION_JSON).body(new User(new UserId(UUID.fromString(id)), "matthew"));
    }

    ServerResponse throwException(ServerRequest request) {
        throw new RuntimeException("error");
    }
}


