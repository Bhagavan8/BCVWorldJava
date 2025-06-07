package com.bcv.world.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseService {

    public List<Map<String, Object>> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        List<Map<String, Object>> users = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Map<String, Object> user = doc.getData();
            users.add(user);
        }
        return users;
    }
}
