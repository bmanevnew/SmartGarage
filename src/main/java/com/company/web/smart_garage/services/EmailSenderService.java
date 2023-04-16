package com.company.web.smart_garage.services;

import jakarta.mail.MessagingException;

public interface EmailSenderService {

    void sendEmail(String toEmail, String subject, String body);

    void sendEmailWithAttachment(String toEmail, String subject, String body, String attachment)
            throws MessagingException;
}
