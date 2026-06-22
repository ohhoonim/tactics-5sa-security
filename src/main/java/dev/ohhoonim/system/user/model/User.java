package dev.ohhoonim.system.user.model;

import java.time.Instant;
import dev.ohhoonim.component.model.unit.BaseEntity;
import dev.ohhoonim.system.user.model.UserComponent.AccessSecurity;
import dev.ohhoonim.system.user.model.UserComponent.AuthorityDelegation;
import dev.ohhoonim.system.user.model.UserComponent.LoginInfo;
import dev.ohhoonim.system.user.model.UserComponent.MenuAction;
import dev.ohhoonim.system.user.model.UserComponent.PasswordCredentials;
import dev.ohhoonim.system.user.model.UserComponent.UserAuthorization;
import dev.ohhoonim.system.user.model.UserComponent.UserProfile;

public class User extends BaseEntity<UserId> {

    private UserProfile profile; // 인적 사항
    private UserStatus status; // 상태 (ACTIVE, LOCK 등)

    private LoginInfo loginInfo; // 로그인 통계 (실패 횟수, 최근 접속 등)
    private PasswordCredentials password; // 비밀번호 및 정책
    private UserAuthorization userAuth; // 권한 및 역할
    private AccessSecurity security; // 보안 및 2FA
    private AuthorityDelegation delegation; // 위임 정보
    private MenuAction accessAction; // 가공된 메뉴 접근 정보

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void userStateTransition(UserTransitionEvent transitionEvent,
            UserStateTransitionPolicy transitionPolicy) {
        User user = this;
        var transitionResult = transitionPolicy.transition(this.status, transitionEvent);
        user.setStatus(transitionResult.status());
        transitionResult.actions().forEach(action -> action.fowloowUp(user));
    }

    public User(UserId userId, String operator) {
        super(userId, operator);
    }

    public static User reconstitute(UserId userId, UserProfile profile, UserStatus status,
            LoginInfo loginInfo, PasswordCredentials password, UserAuthorization userAuth,
            AccessSecurity security, AuthorityDelegation delegation, MenuAction accessAction,
            Instant createdAt, String createdBy, Instant modifiedAt, String modifiedBy) {
        return new User(userId, profile, status, loginInfo, password, userAuth, security,
                delegation, accessAction, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    private User(UserId userId, UserProfile profile, UserStatus status, LoginInfo loginInfo,
            PasswordCredentials password, UserAuthorization userAuth, AccessSecurity security,
            AuthorityDelegation delegation, MenuAction accessAction, Instant createdAt,
            String createdBy, Instant modifiedAt, String modifiedBy) {
        super(userId, createdAt, createdBy, modifiedAt, modifiedBy);
        this.profile = profile;
        this.status = status;
        this.loginInfo = loginInfo;
        this.password = password;
        this.userAuth = userAuth;
        this.security = security;
        this.delegation = delegation;
        this.accessAction = accessAction;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public PasswordCredentials getPassword() {
        return password;
    }

    public UserAuthorization getUserAuth() {
        return userAuth;
    }

    public AccessSecurity getSecurity() {
        return security;
    }

    public AuthorityDelegation getDelegation() {
        return delegation;
    }

    public MenuAction getAccessAction() {
        return accessAction;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public void setPassword(PasswordCredentials password) {
        this.password = password;
    }

    public void setUserAuth(UserAuthorization userAuth) {
        this.userAuth = userAuth;
    }

    public void setSecurity(AccessSecurity security) {
        this.security = security;
    }

    public void setDelegation(AuthorityDelegation delegation) {
        this.delegation = delegation;
    }

    public void setAccessAction(MenuAction accessAction) {
        this.accessAction = accessAction;
    }

    

}
