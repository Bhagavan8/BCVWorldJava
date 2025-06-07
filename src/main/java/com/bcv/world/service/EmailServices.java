package com.bcv.world.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;


@Service
public class EmailServices {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public ResponseEntity<String> sendWelcomeEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // Prepare context with variables
            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("email-template", context);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to BCV World!");
            helper.setText(htmlContent, true);
            helper.setFrom("help.bcv@bcvworld.com");

            mailSender.send(message);
            return ResponseEntity.ok("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while sending email: " + e.getMessage());
        }
    }
}

