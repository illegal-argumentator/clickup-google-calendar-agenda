package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class WebhookVerifier {

    /**
     * Verifies the ClickUp webhook signature.
     *
     * @param secret             The webhook secret value provided by ClickUp
     * @param rawRequestBody     The exact raw body of the webhook POST request
     * @param receivedSignature  The X-Signature header value from the webhook request
     * @return true if the signature is valid, false otherwise
     */
    public static boolean verifySignature(
            String secret,
            String rawRequestBody,
            String receivedSignature
    ) {
        try {
            // Create HMAC with SHA-256
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmacSha256.init(keySpec);

            // Compute the HMAC digest
            byte[] hash = hmacSha256.doFinal(rawRequestBody.getBytes(StandardCharsets.UTF_8));

            // Convert digest to hex string
            StringBuilder hexResult = new StringBuilder();
            for (byte b : hash) {
                hexResult.append(String.format("%02x", b));
            }
            String expectedSignature = hexResult.toString();

            // Compare computed HMAC with header value
            return expectedSignature.equals(receivedSignature);

        } catch (Exception e) {
            // If any error occurs, treat as invalid
            return false;
        }
    }
}

