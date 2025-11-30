package com.finance.hub.service;

import com.finance.hub.dataTransfer.RelationshipDto;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;


    public RelationshipService(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
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

        Relationship saved = relationshipRepository.save(relationship);
        return mapToDto(saved);
    }

//    get RelationshipById
    @Transactional(readOnly = true)
    public RelationshipDto getRelationshipById(Long id){
        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));
        return mapToDto(relationship);
    }

//    Get All Relationships
//    @Transactional(readOnly = true)
//    public List<RelationshipDto> getAllRelationships(){
//        return relationshipRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
//    }

//    Change for Pagination
    @Transactional(readOnly = true)
    public Page<RelationshipDto> getAllRelationships(String search, Pageable pageable) {
        Page<Relationship> relationships;

        if(search == null || search.isBlank()) {
            relationships = relationshipRepository.findAll(pageable);
        } else {
            relationships = relationshipRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrWebsiteContainingIgnoreCase(
                        search, search, search, pageable
            );
        }

        return relationships.map(this::mapToDto);
    }


//    Update Relationship record
    @Transactional
    public RelationshipDto updateRelationship(Long id, RelationshipDto relationshipDto){
        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));

        relationship.setName(relationshipDto.getName());
        relationship.setWebsite(relationshipDto.getWebsite());
        relationship.setEmail(relationshipDto.getEmail());

        Relationship updated = relationshipRepository.save(relationship);
        return mapToDto(updated);
}

//    Delete by Id
    @Transactional
    public void deleteRelationship(Long id){
        if (!relationshipRepository.existsById(id)){
           throw new EntityNotFoundException("Relationship not found with id: " + id);
        }

        relationshipRepository.deleteById(id);
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
