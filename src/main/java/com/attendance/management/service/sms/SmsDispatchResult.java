package com.attendance.management.service.sms;

public record SmsDispatchResult(boolean success, String providerReference, String failureReason) {

    public static SmsDispatchResult success(String providerReference) {
        return new SmsDispatchResult(true, providerReference, null);
    }

    public static SmsDispatchResult failure(String reason) {
        return new SmsDispatchResult(false, null, reason);
    }
}