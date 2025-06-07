package com.bcv.world.service;


import java.util.Properties;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailHostingerService {
	
	public  ResponseEntity<String> sendEmail() {
	    String username = "help.bcv@bcvworld.com";
	    String password = "Sunrise@1823";

	    Properties props = new Properties();
	    props.put("mail.smtp.host", "smtp.hostinger.com");
	    props.put("mail.smtp.port", "465");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.ssl.enable", "true");

	    Session session = Session.getInstance(props, new Authenticator() {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	    });

	    try {
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(username));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bhagavancv@gmail.com"));
	        message.setSubject("Thanks for registration with us!");

	        String htmlMessage = "<!DOCTYPE html>"
	                + "<html><head><style>"
	                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
	                + ".container { background-color: #ffffff; padding: 20px; margin: auto; width: 80%; max-width: 600px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
	                + ".header { text-align: center; }"
	                + ".header img { width: 120px; }"
	                + ".content { text-align: center; padding: 20px; }"
	                + ".content h1 { color: #2c3e50; }"
	                + ".content p { font-size: 16px; color: #555; }"
	                + ".button { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }"
	                + "</style></head><body>"
	                + "<div class='container'>"
	                + "<div class='header'><img src='https://yourdomain.com/logo.png' alt='Logo'></div>"
	                + "<div class='content'>"
	                + "<h1>Welcome to BCV World!</h1>"
	                + "<p>Thank you for registering. You can now log in and start using our services.</p>"
	                + "<a class='button' href='https://www.yourapplicationurl.com'>Login Now</a>"
	                + "</div></div></body></html>";


	        MimeBodyPart mimeBodyPart = new MimeBodyPart();
	        mimeBodyPart.setContent(htmlMessage, "text/html; charset=utf-8");

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(mimeBodyPart);

	        message.setContent(multipart);

	        Transport.send(message);

	        return ResponseEntity.ok("Email sent successfully!");
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
	    }
	}


}
