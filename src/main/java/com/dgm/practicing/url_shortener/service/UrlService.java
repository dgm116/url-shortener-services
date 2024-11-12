package com.dgm.practicing.url_shortener.service;

import com.dgm.practicing.url_shortener.model.UrlModel;

public interface UrlService {

    public abstract String generateUrl(String largeUrl);
    public abstract UrlModel getUrl(String id,int origin);

}
