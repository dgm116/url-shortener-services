package com.dgm.practicing.url_shortener.service.impl;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dgm.practicing.url_shortener.exception.DataProcessingException;
import com.dgm.practicing.url_shortener.exception.ResourceNotFoundException;
import com.dgm.practicing.url_shortener.model.UrlModel;
import com.dgm.practicing.url_shortener.service.UrlService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import lombok.Getter;
import lombok.Setter;

@Service
public class UrlServiceImpl implements UrlService{

    private static final Logger logger = LogManager.getLogger(UrlServiceImpl.class);
    
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    @Getter
    @Setter
    @Value("${base.url}")
    private String baseUrl;
    
    
    private final Firestore db;
    
    public UrlServiceImpl(Firestore db) {
        this.db = db;
    }

    @Override
    public String generateUrl(String largeUrl){

        logger.info("Starting process to generate a unique ID for the URL.");

        String id = "";
        while (id.length() == 0){
            id = generateId().trim();
            if(this.getUrl(id,1) != null){
                logger.warn("Generated ID '{}' already exists. Generating a new ID.", id);
                id = "";
            }
        }

        logger.info("Generated unique ID '{}'. Creating URL model.", id);

        UrlModel um = new UrlModel();
        um.setId(id);
        um.setRealUrl(largeUrl);
        
        DocumentReference dr = db.collection("urls").document(id);
        
        logger.info("Saving URL model to Firestore with ID '{}'.", id);
        ApiFuture<WriteResult> future = dr.set(um);

        try {
            WriteResult result = future.get();
            logger.info("Document with ID '{}' updated successfully at {}", id, result.getUpdateTime());
            return this.baseUrl+um.getId();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error occurred while saving URL with ID '{}': {}", id, e.getMessage(), e);
            throw new DataProcessingException("Error saving data for URL with ID " + id, e);
        }

    }

    @Override
    public UrlModel getUrl(String id,int origin){
        
        UrlModel urlModel = null;
        try{
            logger.info("Starting search for URL with ID '{}'.", id);
            CollectionReference cr = db.collection("urls");
            Query q = cr.whereEqualTo("id", id);
            ApiFuture<QuerySnapshot> apiFuture = q.get();
            QuerySnapshot qs = apiFuture.get();
            if (!qs.isEmpty()) {
                logger.info("Document found for ID '{}'. Mapping to UrlModel.", id);
                DocumentSnapshot document = qs.getDocuments().get(0);
                urlModel = document.toObject(UrlModel.class);
            } else {
                if(origin == 1)
                    return null;
                logger.warn("No document found for ID '{}'.", id);
                throw new ResourceNotFoundException("URL with ID '" + id + "' not found.");
            }

            return urlModel;

        }catch (InterruptedException | ExecutionException e) {
            logger.error("Error occurred while querying URL with ID '{}': {}", id, e.getMessage(), e);
            throw new DataProcessingException("Error querying data for URL with ID " + id, e);
        }
    }

    private String generateId(){
        UUID uuid = UUID.randomUUID();
        return encodeUUID(uuid);
    }

    private String encodeUUID(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        String base62Most = encodeBase62(mostSigBits);
        String base62Least = encodeBase62(leastSigBits);
        return base62Most + base62Least;
    }

    private String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(BASE62.charAt((int) (num % BASE)));
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}
