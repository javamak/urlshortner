package com.mak.sd.urlshortner.service;

import com.mak.sd.urlshortner.ZooKeeperTokenManager;
import com.mak.sd.urlshortner.repo.UrlEntryRepo;
import com.mak.sd.urlshortner.repo.entity.UrlEntry;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortenServiceImpl implements ShortenService {

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.tokenExpiryMonths:60}")
    private Integer tokenExpiryMonths;

    @Autowired
    private UrlEntryRepo repo;

    @Autowired
    private ZooKeeperTokenManager tokenManager;

    private final Hashids hashids = new Hashids("secret_salt", 8);

    @Override
    public String shortenURL(String url) {

        long token = tokenManager.getCurrentToken();
        String hashId = hashids.encode(token);
        System.out.println("current token id:" + token);
        System.out.println("Generated has is:" + hashId);
        LocalDateTime now = LocalDateTime.now();
        UrlEntry entry = UrlEntry.builder().id(hashId)
                .url(url).createdDate(now).hitCount(0).lastHit(now).expiryDate(now.plusMonths(tokenExpiryMonths)).build();
        repo.save(entry);
        return appUrl + entry.getId();
    }

    @Override
    public String expandURL(String id) throws NoSuchElementException {
        Optional<UrlEntry> entryOptional = repo.findById(id);
        UrlEntry entry = entryOptional.orElseThrow();
        entry.setLastHit(LocalDateTime.now());
        entry.setHitCount(entry.getHitCount() + 1);
        repo.save(entry);
        return entry.getUrl();
    }
}
