package com.fontolan.spring.securitykeyvault.example.application.service;

import com.google.api.client.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class TwoFactorAuthService {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSecret() {
        byte[] bytes = new byte[20];
        secureRandom.nextBytes(bytes);
        return Base64.encodeBase64URLSafeString(bytes);
    }

    public boolean verifyCode(String secret, int code) {
        long timeIndex = System.currentTimeMillis() / 1000 / 30;
        byte[] key = Base64.decodeBase64(secret);
        try {
            for (int i = -1; i <= 1; i++) {
                if (generateCode(key, timeIndex + i) == code) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private int generateCode(byte[] key, long timestep) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timestep);
        byte[] data = buffer.array();

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        int truncatedHash = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);
        return truncatedHash % 1000000;
    }
}
