package com.company.web.smart_garage.services;

import com.company.web.smart_garage.services.impl.EmailSenderServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class EmailSenderServiceTests {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderServiceImpl emailSenderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendEmail_Success() {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("smartGarageRepairs@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        emailSenderService.sendEmail(toEmail, subject, body);

        verify(mailSender, Mockito.times(1)).send(Mockito.eq(message));
    }

    @Test
    public void sendEmailWithAttachment_Success() throws MessagingException, IOException {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        byte[] attachmentData = "This is the attachment content".getBytes();
        ByteArrayResource attachmentResource = new ByteArrayResource(attachmentData);

        EmailSenderServiceImpl emailSenderService = new EmailSenderServiceImpl(mailSender);
        emailSenderService.sendEmailWithAttachment("test@example.com", "Test Email",
                "Test Body", "/path/to/file.txt", attachmentResource);

        verify(mailSender, times(1)).send(mimeMessage);
        verify(mimeMessage, times(1)).setContent(any(Multipart.class));
    }
}
