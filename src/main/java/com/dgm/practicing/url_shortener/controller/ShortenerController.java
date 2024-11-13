package com.dgm.practicing.url_shortener.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dgm.practicing.url_shortener.dto.ShortenUrlResponseDto;
import com.dgm.practicing.url_shortener.service.UrlService;




@RestController
@RequestMapping("/st1")
public class ShortenerController {

    private static final Logger logger = LogManager.getLogger(ShortenerController.class);
    
    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponseDto> getShorterUrl(@RequestParam String url) {
        logger.info("Request received toshorten URL: {}", url);
        String shorterUrl;

        try{
            shorterUrl = urlService.generateUrl(url);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        ShortenUrlResponseDto responseDto = new ShortenUrlResponseDto(shorterUrl);

        logger.info("Successfully generated shortened URL: {}", shorterUrl);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String id) {

        logger.info("Request received to redirect ID: {}", id);

        String longUrl = urlService.getUrl(id,0).getRealUrl();

        if (longUrl != null) {
            logger.info("Redirection found for ID: {}, redirecting to URL: {}", id, longUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", longUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            logger.warn("No URL found for ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
