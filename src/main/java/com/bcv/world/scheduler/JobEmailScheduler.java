package com.bcv.world.scheduler;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bcv.world.model.JobDto;
import com.bcv.world.service.EmailServices;
import com.bcv.world.service.EmailStatusService;
import com.bcv.world.service.FirebaseJobService;
import com.bcv.world.service.FirebaseService;

@Component
public class JobEmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobEmailScheduler.class);

    @Autowired
    private FirebaseService firebaseUserService;

    @Autowired
    private FirebaseJobService firebaseJobService;

    @Autowired
    private EmailServices mailService;

    @Autowired
    private EmailStatusService statusService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void sendEmails() throws ExecutionException, InterruptedException {

        List<String> emails = firebaseUserService.getUserEmails();
        List<JobDto> jobs = firebaseJobService.fetchRecentJobs();

        if (emails.isEmpty() || jobs.isEmpty()) {
            log.warn("No users or jobs found. Skipping email sending.");
            return;
        }

        int batchSize = 30; // SAFE for Hostinger
        String adminEmail = "help.bcv@bcvworld.com";

        for (int i = 0; i < emails.size(); i += batchSize) {

            List<String> batch =
                    emails.subList(i, Math.min(i + batchSize, emails.size()));

            boolean batchSent = false;

            try {
                mailService.sendJobMailBcc(adminEmail, batch, jobs);
                batchSent = true;
                log.info("Email batch sent successfully ({} users)", batch.size());

            } catch (Exception e) {
                log.error("Failed to send email batch ({} users)", batch.size(), e);
            }

            // ✅ SAVE STATUS PER USER & PER JOB
            for (String email : batch) {
                for (JobDto job : jobs) {
                    statusService.saveStatus(
                            email,
                            job,
                            batchSent ? "SENT" : "FAILED"
                    );
                }
            }

            // ⏱️ VERY IMPORTANT — COOL DOWN
            Thread.sleep(15000); // 15 seconds
        }
    }


}
