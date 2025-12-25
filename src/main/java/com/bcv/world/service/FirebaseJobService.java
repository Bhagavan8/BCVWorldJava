package com.bcv.world.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bcv.world.model.JobDto;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseJobService {

    private static final Logger log =
            LoggerFactory.getLogger(FirebaseJobService.class);

    /**
     * Fetch jobs created in the last 48 hours (Today + Yesterday)
     */
    public List<JobDto> fetchRecentJobs()
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection("jobs").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        ZoneId zoneId = ZoneId.systemDefault();
        Instant now = Instant.now();
        Instant cutoff = now.minus(48, ChronoUnit.HOURS);

        List<JobDto> jobs = new ArrayList<>();

        log.info("Fetching jobs from Firestore. Total docs: {}", documents.size());

        for (QueryDocumentSnapshot doc : documents) {

            Map<String, Object> data = doc.getData();

            // ---- company name ----
            String companyName = (String) data.get("companyName");
            String companyId = (String) data.get("companyId");

            if ((companyName == null || companyName.isBlank()) && companyId != null) {
                companyName = fetchCompanyNameById(db, companyId);
            }

            if (companyName == null || companyName.isBlank()) {
                log.warn("Skipping job {} – company name not found", doc.getId());
                continue;
            }

            // ---- createdAt ----
            Object createdAtObj = data.get("createdAt");
            if (createdAtObj == null) {
                log.warn("Skipping job {} – createdAt missing", doc.getId());
                continue;
            }

            try {
                Instant jobInstant = parseCreatedAt(createdAtObj);

                if (jobInstant == null || jobInstant.isBefore(cutoff)) {
                    continue;
                }

                LocalDate jobDate = jobInstant
                        .atZone(zoneId)
                        .toLocalDate();

                // ✅ FIXED JOB LINK (DOC ID BASED)
                String jobLink =
                        "https://bcvworld.com/html/job-details.html?id="
                        + doc.getId()
                        + "&type=private";

                jobs.add(new JobDto(companyName, jobLink, jobDate));

            } catch (Exception e) {
                log.error(
                    "Failed to process job {} with createdAt={}",
                    doc.getId(),
                    createdAtObj,
                    e
                );
            }
        }

        log.info("Jobs found in last 48 hours: {}", jobs.size());
        return jobs;
    }

    /**
     * Fetch company name from companies collection using companyId
     */
    private String fetchCompanyNameById(Firestore db, String companyId) {
        try {
            DocumentSnapshot doc =
                    db.collection("companies")
                      .document(companyId)
                      .get()
                      .get();

            if (doc.exists()) {
                return doc.getString("name");
            }

        } catch (Exception e) {
            log.error("Failed to fetch company for companyId {}", companyId, e);
        }

        return null;
    }

    /**
     * Robust createdAt parser
     */
    private Instant parseCreatedAt(Object createdAtObj) {
        try {
            if (createdAtObj instanceof Timestamp ts) {
                return ts.toDate().toInstant();
            }

            if (createdAtObj instanceof String str) {
                // Fix malformed timestamps like T0:03
                if (str.matches(".*T\\d:.*")) {
                    str = str.replaceFirst("T(\\d):", "T0$1:");
                }
                return Instant.parse(str);
            }
        } catch (Exception e) {
            log.error("Invalid createdAt value: {}", createdAtObj, e);
        }
        return null;
    }
}
