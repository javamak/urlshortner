package com.mak.sd.urlshortner.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UrlEntry {

    @PrimaryKey
    private String id;
    private String url;
    private Integer hitCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastHit;
    private LocalDateTime expiryDate;
}
