package com.attendance.management.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Used whenever app.sms.api-key isn't configured. Never reports success -
// nothing was actually sent, so the honest result is failure, which leaves
// the notification row at PENDING/FAILED where it belongs.
public class LoggingSmsProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsProvider.class);

    @Override
    public SmsDispatchResult send(String toPhoneNumber, String message) {
        log.info("[SMS not sent - no provider configured] To: {} | Message: {}", toPhoneNumber, message);
        return SmsDispatchResult.failure("No SMS provider is configured (app.sms.api-key is not set).");
    }
}