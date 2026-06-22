@org.springframework.modulith.ApplicationModule(id = "system-security", displayName = "인증인가",
        allowedDependencies = {"component::*", "user::*", "system-audit"})
package dev.ohhoonim.system.security;
