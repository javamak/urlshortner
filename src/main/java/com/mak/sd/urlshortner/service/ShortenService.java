package com.mak.sd.urlshortner.service;

public interface ShortenService {
    String shortenURL(String url);

    String expandURL(String id);
}
