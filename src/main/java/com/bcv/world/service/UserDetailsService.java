package com.bcv.world.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bcv.world.model.UserEmailStatus;
import com.bcv.world.repository.UserEmailStatusRepository;

@Service
public class UserDetailsService {

	@Autowired
	private UserEmailStatusRepository userRepo;

	@Autowired
	private EmailServices emailService;

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);
	int sentCount = 0;

	public void insertUserDetails(String email, String name) {
		if (!userRepo.existsById(email)) {
			UserEmailStatus newUser = new UserEmailStatus();
			newUser.setEmail(email);
			newUser.setName(name.trim());
			newUser.setEmailSent(false); // default
			userRepo.save(newUser);
			logger.info("Inserted new user: {}", email);
		} else {
			logger.info("Already exists email so skipping : {}", email);
		}
	}

	public ResponseEntity<Integer> findEmailIDSendEmail() {
		List<UserEmailStatus> pendingEmails = userRepo.findAll().stream().filter(u -> !u.isEmailSent()).toList();

		for (UserEmailStatus user : pendingEmails) {
			ResponseEntity<String> response = emailService.sendWelcomeEmail(user.getEmail(), user.getName());

			if (response.getStatusCode().is2xxSuccessful()) {
				user.setEmailSent(true);
				userRepo.save(user);
				sentCount++;
				logger.info("Email sent to: {}", user.getEmail());
			} else {
				logger.error("Failed to send email to: {} - {}", user.getEmail(), response.getBody());
			}
		}
		return ResponseEntity.ok(sentCount);
	}

}
