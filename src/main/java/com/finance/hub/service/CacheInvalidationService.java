package com.finance.hub.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheInvalidationService {

    @CacheEvict(value = "contacts", allEntries = true)
    public void evictContactsCache(){
        //spring handles eviction
    }

    @CacheEvict(value = "financialAccounts", allEntries = true)
    public void evictFinancialAccountsCache(){
        //spring handles eviction
    }

    @CacheEvict(value = "relationships", allEntries = true)
    public void evictRelationshipsCache(){
        //spring handles eviction
    }

}
