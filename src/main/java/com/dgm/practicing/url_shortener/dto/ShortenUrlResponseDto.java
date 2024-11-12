package com.dgm.practicing.url_shortener.dto;

import lombok.Getter;
import lombok.Setter;

public class ShortenUrlResponseDto {

    @Getter
    @Setter
    private String shortenURL;

    public ShortenUrlResponseDto(String shorterURL){
        this.shortenURL = shorterURL;
    }
}
