package com.finance.hub.service;

import com.finance.hub.dataTransfer.RelationshipDto;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.jdbcRepo.RelationshipJdbcRepository;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RelationshipService {

    private final RelationshipJdbcRepository relationshipJdbcRepository;
//    private final RelationshipRepository relationshipRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public RelationshipService(RelationshipJdbcRepository relationshipJdbcRepository, CacheInvalidationService cacheInvalidationService) {
        this.relationshipJdbcRepository = relationshipJdbcRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

//    Create Relationship
    @Transactional
    public RelationshipDto createRelationship(RelationshipDto relationshipDto){
        Relationship relationship = new Relationship(
                relationshipDto.getId(),
                relationshipDto.getName(),
                relationshipDto.getWebsite(),
                relationshipDto.getEmail()
        );

        Relationship saved = relationshipJdbcRepository.save(relationship);

        cacheInvalidationService.evictFinancialAccountsCache();
        //clear getAllRelationships cache after create

        return mapToDto(saved);
    }

//    get RelationshipById
    @Transactional(readOnly = true)
    public RelationshipDto getRelationshipById(Long id){
        Relationship relationship = relationshipJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));
        return mapToDto(relationship);
    }


//    Change for Pagination
    @Cacheable("relationships")
    @Transactional(readOnly = true)
    public Page<RelationshipDto> getAllRelationships(String search, int limit, int offset, String sortBy, String direction) {

        List<Relationship> relationships;

        if(search == null || search.isBlank()) {
            relationships = relationshipJdbcRepository.findAll(limit, offset, sortBy, direction);
        } else {
            relationships = relationshipJdbcRepository
                    .searchRelationships(
                        search, limit, offset, sortBy, direction);
        }

        List<RelationshipDto> dtos = relationships.stream()
                .map(this::mapToDto)
                .toList();

        Long total = relationshipJdbcRepository.countRelationships(search);

        PageRequest pageRequest = PageRequest.of(offset / limit, limit);

        return new PageImpl<>(dtos, pageRequest, total);
    }


//    Update Relationship record
    @Transactional
    public RelationshipDto updateRelationship(Long id, RelationshipDto relationshipDto){
        Relationship relationship = relationshipJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));

        relationship.setName(relationshipDto.getName());
        relationship.setWebsite(relationshipDto.getWebsite());
        relationship.setEmail(relationshipDto.getEmail());

        Relationship updated = relationshipJdbcRepository.save(relationship);

        cacheInvalidationService.evictFinancialAccountsCache();
        //clear getAllRelationships cache after Update

        return mapToDto(updated);
}

//Delete a contact -> One DB delete. Wraps delete in a transaction.
    @Transactional
    public void deleteRelationship(Long id){
        if (!relationshipJdbcRepository.existsById(id)){
           throw new EntityNotFoundException("Relationship not found with id: " + id);
        }

        relationshipJdbcRepository.deleteById(id);

        cacheInvalidationService.evictFinancialAccountsCache();
        //clear getAllRelationships cache after delete
    }



    private RelationshipDto mapToDto(Relationship relationship){
        return new RelationshipDto(
                relationship.getId(),
                relationship.getName(),
                relationship.getWebsite(),
                relationship.getEmail()
        );
    }
}
