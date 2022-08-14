package com.mak.sd.urlshortner.repo;

import com.mak.sd.urlshortner.repo.entity.UrlEntry;
import org.springframework.data.repository.CrudRepository;

public interface UrlEntryRepo extends CrudRepository<UrlEntry, String> {
}
