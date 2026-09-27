package com.attendance.management.service.sms;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.Map;

// Generic JSON-over-HTTPS template: Bearer auth, {to, message, senderId} body,
// any 2xx = success. This has NOT been validated against a specific vendor's
// exact contract - adjust the body shape/auth header to match whichever
// provider you actually configure (MSG91, Twilio, Fast2SMS, etc).
public class HttpSmsProvider implements SmsProvider {

    private final RestClient restClient;
    private final String providerUrl;
    private final String apiKey;
    private final String senderId;

    public HttpSmsProvider(String providerUrl, String apiKey, String senderId) {
        this.providerUrl = providerUrl;
        this.apiKey = apiKey;
        this.senderId = senderId;
        this.restClient = RestClient.create();
    }

    @Override
    public SmsDispatchResult send(String toPhoneNumber, String message) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("to", toPhoneNumber);
        requestBody.put("message", message);
        if (senderId != null && !senderId.isBlank()) {
            requestBody.put("senderId", senderId);
        }

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(providerUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);

            // retrieve() throws on non-2xx before reaching here, so getting this
            // far means the provider accepted the request.
            return SmsDispatchResult.success(response.getBody());

        } catch (RestClientException ex) {
            return SmsDispatchResult.failure(ex.getMessage());
        }
    }
}