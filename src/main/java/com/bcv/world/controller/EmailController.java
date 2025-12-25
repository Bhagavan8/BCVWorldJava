package com.bcv.world.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcv.world.scheduler.JobEmailScheduler;
import com.bcv.world.service.EmailServices;
import com.bcv.world.service.FirebaseService;
import com.bcv.world.service.UserDetailsService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/email")
public class EmailController {

	@Autowired
	private EmailServices emailService;
	@Autowired
	private FirebaseService firebaseService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JobEmailScheduler scheduler;

	@GetMapping("/send")
	public ResponseEntity<String> sendEmail() throws MessagingException {
		return emailService.sendWelcomeEmail("bhagavancv@gmail.com", "Bhagavan");
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

	@GetMapping("/send-all")
	public ResponseEntity<String> sendEmailsToAll() {

		try {
			List<Map<String, Object>> users = firebaseService.getAllUsers();
			logger.info("Fetched {} users from Firebase", users.size());

			for (Map<String, Object> user : users) {
				String email = (String) user.get("email");
				String firstName = (String) user.get("firstName");
				String lastName = (String) user.get("lastName");
				String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

				if (email == null || name.trim().isEmpty()) {
					logger.warn("Skipped user with missing email or name: {}", user);
					continue;
				}

				userDetailsService.insertUserDetails(email, name);
			}

			Integer sentCount = 0;

			ResponseEntity<Integer> responseEntity = userDetailsService.findEmailIDSendEmail();
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				sentCount = responseEntity.getBody();
				System.out.println("Total emails sent: " + sentCount);
			} else {
				System.out.println("Failed to send emails");
			}

			return ResponseEntity.ok("Emails sent: " + sentCount);

		} catch (Exception e) {
			logger.error("Error during email processing", e);
			return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}

	@GetMapping("/sendJobs")
	public String triggerEmailManually() throws ExecutionException, InterruptedException {
		scheduler.sendEmails();
		return "Job email process triggered successfully";
	}

}
