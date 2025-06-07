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
public class EmailService {

	public ResponseEntity<String> sendEmail() throws MessagingException {
		try {

			String username = "chithrabeeragownivari@gmail.com";
			String password = "wwkl mrax ksql axdv";

			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("from@gmail.com"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bhagavancv@gmail.com"));
				message.setSubject("Thanks for registration with us!");

				String htmlMessage = "<h1>Hearthly Welcome!</h1>"
						+ "<p>Thank you for registering with our application. You can now login and start using our services.</p>"
						+ "<a href='https://www.yourapplicationurl.com'>Click here to login</a>";

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
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
		}
	}

}
