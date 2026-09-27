package com.attendance.management.service.sms;

public interface SmsProvider {

    SmsDispatchResult send(String toPhoneNumber, String message);
}