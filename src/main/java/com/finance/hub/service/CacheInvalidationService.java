package com.finance.hub.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheInvalidationService {

    @CacheEvict(value = "contacts", allEntries = true)
    public void evictContactsCache(){
        //spring handles eviction
    }
}
