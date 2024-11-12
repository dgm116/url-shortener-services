package com.dgm.practicing.url_shortener.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dgm.practicing.url_shortener.exception.ResourceNotFoundException;
import com.dgm.practicing.url_shortener.model.UrlModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    

    @Mock
    private Firestore db;

    @Mock
    private QuerySnapshot mockQuerySnapshot;
    
    @Mock
    private QueryDocumentSnapshot mockDocumentSnapshot;
    
    @Mock
    private ApiFuture<QuerySnapshot> mockApiFuture;

    @Mock
    private DocumentReference mockDocumentReference;

    @Mock
    private ApiFuture<WriteResult> mockApiFutureWriteResult;

    @Mock
    private WriteResult mockWriteResult;
    
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
    }
    

    @DisplayName("Test for found id")
    @Test
    void testGetUrlFoundUrl() throws InterruptedException, ExecutionException {
        String id = "123";
        int origin = 1;
        
        when(db.collection("urls")).thenReturn(mock(CollectionReference.class));
        when(db.collection("urls").whereEqualTo("id", id)).thenReturn(mock(Query.class));
        when(db.collection("urls").whereEqualTo("id", id).get()).thenReturn(mockApiFuture);
        when(mockApiFuture.get()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.isEmpty()).thenReturn(false);
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        documents.add(mockDocumentSnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);
        UrlModel um = UrlModel.builder()
                                    .id("123")
                                    .realUrl("htpp://prueba.com")
                                    .build();
        when(mockDocumentSnapshot.toObject(UrlModel.class)).thenReturn(um);
        
        urlService = new UrlServiceImpl(db);
        UrlModel urlModel = urlService.getUrl(id, origin);

        assertNotNull(urlModel);
        assertEquals(id, urlModel.getId());
    }
    
    
    @DisplayName("Test for not found id")
    @Test
    void testGetUrlNotFoundUrl() throws InterruptedException, ExecutionException {
        String id = "123";
        int origin = 1;

        when(db.collection("urls")).thenReturn(mock(CollectionReference.class));
        when(db.collection("urls").whereEqualTo("id", id)).thenReturn(mock(Query.class));
        when(db.collection("urls").whereEqualTo("id", id).get()).thenReturn(mockApiFuture);
        when(mockApiFuture.get()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.isEmpty()).thenReturn(true);
        
        urlService = new UrlServiceImpl(db);
        UrlModel urlModel = urlService.getUrl(id, origin);

        assertNull(urlModel);
    }

    @DisplayName("Test for not found id and origin is 0")
    @Test
    void testGetUrlNotFoundUrlOriginZero() throws InterruptedException, ExecutionException {
        String id = "123";
        int origin = 0;

        when(db.collection("urls")).thenReturn(mock(CollectionReference.class));
        when(db.collection("urls").whereEqualTo("id", id)).thenReturn(mock(Query.class));
        when(db.collection("urls").whereEqualTo("id", id).get()).thenReturn(mockApiFuture);
        when(mockApiFuture.get()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.isEmpty()).thenReturn(true);
        
        urlService = new UrlServiceImpl(db);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            urlService.getUrl(id, origin);
        });
    }

    @DisplayName("Test for successful Case")
    @Test
    void testGenerateUrlSuccess() throws InterruptedException, ExecutionException{
        urlService = spy(new UrlServiceImpl(db));
        urlService.setBaseUrl("https://base.yl/");

        doReturn(null).when(urlService).getUrl(anyString(), anyInt());

        when(db.collection("urls")).thenReturn(mock(CollectionReference.class));
        when(db.collection("urls").document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(any(UrlModel.class))).thenReturn(mockApiFutureWriteResult);
        when(mockApiFutureWriteResult.get()).thenReturn(mockWriteResult);
        when(mockWriteResult.getUpdateTime()).thenReturn(Timestamp.now());

        String largeUrl = "https://prueba.url.larga/getSuccess";
        String resultUrl = urlService.generateUrl(largeUrl);

        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains(urlService.getBaseUrl()));
    }
        
}