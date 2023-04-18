package com.company.web.smart_garage.services;

import jakarta.mail.MessagingException;
import org.springframework.core.io.ByteArrayResource;

public interface EmailSenderService {

    void sendEmail(String toEmail, String subject, String body);

    void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentFilename,
                                 ByteArrayResource attachmentResource) throws MessagingException;

}
