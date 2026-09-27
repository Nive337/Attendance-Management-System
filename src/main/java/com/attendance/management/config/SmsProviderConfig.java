package com.attendance.management.config;

import com.attendance.management.service.sms.HttpSmsProvider;
import com.attendance.management.service.sms.LoggingSmsProvider;
import com.attendance.management.service.sms.SmsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsProviderConfig {

    @Bean
    public SmsProvider smsProvider(@Value("${app.sms.provider-url:}") String providerUrl,
                                    @Value("${app.sms.api-key:}") String apiKey,
                                    @Value("${app.sms.sender-id:}") String senderId) {
        if (apiKey == null || apiKey.isBlank()) {
            return new LoggingSmsProvider();
        }
        if (providerUrl == null || providerUrl.isBlank()) {
            throw new IllegalStateException(
                    "app.sms.api-key is set but app.sms.provider-url is missing. " +
                    "Set SMS_PROVIDER_URL, or unset SMS_API_KEY to run without live SMS dispatch.");
        }
        return new HttpSmsProvider(providerUrl, apiKey, senderId);
    }
}