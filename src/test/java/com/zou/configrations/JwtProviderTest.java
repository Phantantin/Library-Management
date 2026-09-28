package com.zou.configrations;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    @Test
    void generatesAnEphemeralDevelopmentKeyWhenSecretIsMissing() {
        JwtProvider provider = new JwtProvider("", false);
        var authentication = new UsernamePasswordAuthenticationToken("reader@example.com", null, List.of());

        String token = provider.generateToken(authentication);

        assertThat(provider.getEmailFromJwtToken("Bearer " + token)).isEqualTo("reader@example.com");
    }

    @Test
    void requiresAConfiguredSecretWhenProductionGuardIsEnabled() {
        assertThatThrownBy(() -> new JwtProvider("", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    void rejectsConfiguredSecretsThatAreTooShort() {
        assertThatThrownBy(() -> new JwtProvider("short", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 32");
    }
}
