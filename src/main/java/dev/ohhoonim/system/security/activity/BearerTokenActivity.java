package dev.ohhoonim.system.security.activity;

import dev.ohhoonim.system.security.model.JwtToken;

public interface BearerTokenActivity {

    JwtToken verifyWith(String token);

}
