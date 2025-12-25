package com.bcv.world.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseService.class);

    public List<Map<String, Object>> getAllUsers()
            throws ExecutionException, InterruptedException {

        log.info("Fetching users from Firestore collection: users");

        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        log.info("Total documents fetched from Firestore: {}", documents.size());

        List<Map<String, Object>> users = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Map<String, Object> user = doc.getData();
            users.add(user);
        }

        log.info("Users data mapping completed");
        return users;
    }

    public List<String> getUserEmails()
            throws ExecutionException, InterruptedException {

        log.info("Extracting user emails from Firestore data");

        List<Map<String, Object>> users = getAllUsers();

        List<String> emails = users.stream()
                .map(user -> (String) user.get("email"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        log.info("Total valid email IDs fetched: {}", emails.size());

        if (emails.isEmpty()) {
            log.warn("No user emails found in Firestore");
        }

        return emails;
    }
}
