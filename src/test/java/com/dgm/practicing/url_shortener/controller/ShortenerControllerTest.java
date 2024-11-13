package com.dgm.practicing.url_shortener.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dgm.practicing.url_shortener.dto.ShortenUrlResponseDto;
import com.dgm.practicing.url_shortener.model.UrlModel;
import com.dgm.practicing.url_shortener.service.UrlService;

@ExtendWith(MockitoExtension.class)
public class ShortenerControllerTest {

    @Mock
    UrlService mockUrlService;

    @InjectMocks
    ShortenerController mockShortenerController;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Test for successful shortening of URL")
    @Test
    void testGetShorterUrlSuccess() {

        String longUrl = "https://prueba.url.larga/getSuccess";
        String shortenedUrl = "https://base.yl/123";

        when(mockUrlService.generateUrl(longUrl)).thenReturn(shortenedUrl);

        ResponseEntity<ShortenUrlResponseDto> response = mockShortenerController.getShorterUrl(longUrl);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shortenedUrl, response.getBody().getShortenURL());
    }

    @DisplayName("Test for error in shortening URL")
    @Test
    void testGetShorterUrlError() {
        String longUrl = "https://prueba.url.larga/getError";
        
        when(mockUrlService.generateUrl(longUrl)).thenThrow(new RuntimeException("Error generating URL"));

        ResponseEntity<ShortenUrlResponseDto> response = mockShortenerController.getShorterUrl(longUrl);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @DisplayName("Test for successful redirection to long URL")
    @Test
    void testRedirectToLongUrlSuccess() {
        
        String id = "123";
        String longUrl = "https://prueba.url.larga/getSuccess";
        
        UrlModel urlModel = UrlModel.builder()
                                    .id(id)
                                    .realUrl(longUrl).
                                    build();

        when(mockUrlService.getUrl(id, 0)).thenReturn(urlModel);

        ResponseEntity<Void> response = mockShortenerController.redirectToLongUrl(id);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Location"));
        assertEquals(longUrl, response.getHeaders().get("Location").get(0));
    }

    @DisplayName("Test for ID not found for redirection")
    @Test
    void testRedirectToLongUrlNotFound() {

        String id = "123";
        
        when(mockUrlService.getUrl(id, 0)).thenReturn(mock(UrlModel.class));
        when(mockUrlService.getUrl(id, 0).getRealUrl()).thenReturn(null);

        ResponseEntity<Void> response = mockShortenerController.redirectToLongUrl(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
