package com.dgm.practicing.url_shortener.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UrlModel {

    private String id;

    private String realUrl;

    public UrlModel(){}

    public UrlModel(String id, String realUrl) {
        this.id = id;
        this.realUrl = realUrl;
    }

}
