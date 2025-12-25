package com.bcv.world.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bcv.world.model.JobDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServices {

    private static final Logger log = LoggerFactory.getLogger(EmailServices.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // 🔹 Testing email configuration
    @Value("${mail.test.enabled:false}")
    private boolean testMailEnabled;

    @Value("${mail.test.address:}")
    private String testMailAddress;

    // 🔹 WELCOME EMAIL (only logger added, logic unchanged)
    public ResponseEntity<String> sendWelcomeEmail(String toEmail, String name) {

        log.info("Sending welcome email to {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                            StandardCharsets.UTF_8.name()
                    );

            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("email-template", context);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to BCV World!");
            helper.setText(htmlContent, true);
            helper.setFrom("help.bcv@bcvworld.com");

            mailSender.send(message);

            log.info("Welcome email sent successfully to {}", toEmail);
            return ResponseEntity.ok("Email sent successfully!");

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}", toEmail, e);
            return ResponseEntity.status(500)
                    .body("Error while sending email: " + e.getMessage());
        }
    }

 // 🔥 JOB EMAIL (HTML + BCC + Test Mode Supported)
    public void sendJobMailBcc(
            String adminEmail,
            List<String> userEmails,
            List<JobDto> jobs) {

        log.info("Preparing BCC job email. Users count: {}", userEmails.size());

        // ✅ Test email override
        if (testMailEnabled && testMailAddress != null && !testMailAddress.isBlank()) {
            log.warn("TEST MODE ENABLED – Overriding all emails to {}", testMailAddress);
            userEmails = List.of(testMailAddress);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail); // 👈 Admin email
            helper.setBcc(userEmails.toArray(new String[0])); // 👈 Hidden recipients
            helper.setSubject("Today’s Job Openings");
            helper.setFrom("help.bcv@bcvworld.com");

            // ✅ HTML content
            helper.setText(buildJobEmailHtml(jobs), true);

            mailSender.send(message);

            log.info("BCC job email sent successfully");

        } catch (Exception e) {
            log.error("Failed to send BCC job email", e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildJobEmailHtml(List<JobDto> jobs) {

        StringBuilder html = new StringBuilder();

        html.append("""
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background:#f4f6f8; font-family:Arial,sans-serif;">
              <table width="100%" cellpadding="0" cellspacing="0" style="padding:20px;">
                <tr>
                  <td align="center">

                    <table width="600" cellpadding="0" cellspacing="0"
                           style="background:#ffffff; border-radius:8px; overflow:hidden;">

                      <tr>
                        <td style="background:#0a66c2; color:#ffffff; padding:20px; text-align:center;">
                          <h2 style="margin:0;">Today’s Job Openings</h2>
                          <p style="margin:6px 0 0; font-size:14px;">
                            Latest opportunities curated for you
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:20px;">
            """);

        for (JobDto job : jobs) {
            html.append("""
                <div style="margin-bottom:16px; padding-bottom:16px; border-bottom:1px solid #eeeeee;">
                  <h3 style="margin:0 0 6px; color:#333333;">%s</h3>
                  <a href="%s"
                     style="color:#0a66c2; font-weight:bold; text-decoration:none;">
                     👉 Apply Now
                  </a>
                </div>
            """.formatted(job.getCompany(), job.getLink()));
        }

        html.append("""
                        </td>
                      </tr>

                      <tr>
                        <td style="background:#f9f9f9; padding:15px; text-align:center;
                                   font-size:12px; color:#666;">
                          <p style="margin:0;">
                            You are receiving this email because you subscribed to job alerts on
                            <strong>BCVWorld</strong>.
                          </p>
                          <p style="margin:6px 0 0;">© 2025 BCVWorld</p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>
            </body>
            </html>
        """);

        return html.toString();
    }

}
