package dev.ohhoonim.system.auditlog.endpoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import dev.ohhoonim.component.model.paging.PageRequest;
import dev.ohhoonim.component.model.unit.Endpoint;
import dev.ohhoonim.system.auditlog.application.AuditLogReadService;
import dev.ohhoonim.system.auditlog.application.AuditLogSearchRequest;
import dev.ohhoonim.system.auditlog.application.UserRequester;
import dev.ohhoonim.system.auditlog.model.AuditLogId;

@Component
public class AuditLogHandler implements Endpoint {

    private final AuditLogReadService auditLogReadService;

    public AuditLogHandler(AuditLogReadService auditLogReadService) {
        this.auditLogReadService = auditLogReadService;
    }

    public ServerResponse getDetail(ServerRequest request) {
        String id = request.pathVariable("id");
        UserRequester requester = buildUserRequester(request);
        var response = auditLogReadService.getDetail(AuditLogId.Creator.fromPublic(id), requester);
        return ServerResponse.ok().body(response);
    }

    public ServerResponse getLogsByTarget(ServerRequest request) {
        String targetType = request.pathVariable("targetType");
        String targetId = request.param("targetId").orElse(null);
        AuditLogSearchRequest searchRequest = buildSearchRequest(request);
        PageRequest pageRequest = buildPageRequest(request);
        
        var response = auditLogReadService.getLogsByTarget(targetType, targetId, searchRequest, pageRequest);
        return ServerResponse.ok().body(response);
    }

    public ServerResponse getLogsByActor(ServerRequest request) {
        String actorId = request.pathVariable("actorId");
        AuditLogSearchRequest searchRequest = buildSearchRequest(request);
        PageRequest pageRequest = buildPageRequest(request);
        
        var response = auditLogReadService.getLogsByActor(actorId, searchRequest, pageRequest);
        return ServerResponse.ok().body(response);
    }

    private UserRequester buildUserRequester(ServerRequest request) {
        // userId assumes a security filter populates this attribute
        String userId = (String) request.attribute("userId").orElse("SYSTEM");
        // roles, username, etc. could be extracted from SecurityContext or attributes
        // For simplicity and alignment with functional endpoint pattern, we use attributes
        return new UserRequester(
            userId, 
            (String) request.attribute("username").orElse("unknown"),
            (Set<String>) request.attribute("roles").orElse(Set.of()),
            request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse("127.0.0.1"),
            LocalDateTime.now()
        );
    }

    private PageRequest buildPageRequest(ServerRequest request) {
        int pageNo = request.param("pageNo").map(Integer::parseInt).orElse(1);
        int pageSize = request.param("pageSize").map(Integer::parseInt).orElse(10);
        return new PageRequest(pageNo, pageSize);
    }

    private AuditLogSearchRequest buildSearchRequest(ServerRequest request) {
        LocalDate startDate = request.param("startDate").map(LocalDate::parse).orElse(null);
        LocalDate endDate = request.param("endDate").map(LocalDate::parse).orElse(null);
        String actionCategory = request.param("actionCategory").orElse(null);
        String actionType = request.param("actionType").orElse(null);
        String resultStatus = request.param("resultStatus").orElse(null);
        String actorId = request.param("actorId").orElse(null);
        String targetId = request.param("targetId").orElse(null);
        String targetType = request.param("targetType").orElse(null);

        return new AuditLogSearchRequest(
            startDate, endDate, actionCategory, actionType, resultStatus, actorId, targetId, targetType
        );
    }
}
