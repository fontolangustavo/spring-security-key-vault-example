package com.fontolan.spring.securitykeyvault.example.application.service;

import com.google.api.client.util.Base64;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class TwoFactorAuthServiceTest {

    @Test
    void secretIsBase64() {
        TwoFactorAuthService service = new TwoFactorAuthService();
        String secret = service.generateSecret();
        assertThat(Base64.decodeBase64(secret)).isNotEmpty();
    }

    @Test
    void verifyCodeUsesGeneratedCode() throws Exception {
        TwoFactorAuthService service = new TwoFactorAuthService();
        String secret = service.generateSecret();
        byte[] key = Base64.decodeBase64(secret);

        Method m = TwoFactorAuthService.class.getDeclaredMethod("generateCode", byte[].class, long.class);
        m.setAccessible(true);
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        int code = (int) m.invoke(service, key, timeIndex);

        assertThat(service.verifyCode(secret, code)).isTrue();
        assertThat(service.verifyCode(secret, 999999)).isFalse();
    }
}
