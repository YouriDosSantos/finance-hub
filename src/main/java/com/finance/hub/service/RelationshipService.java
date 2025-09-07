package com.finance.hub.service;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.dataTransfer.RelationshipDto;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<RelationshipDto> getRelationshipById(Long id){
        return relationshipRepository.findById(id).map(this::mapToDto);
    }

//    Get All Relationships
    @Transactional(readOnly = true)
    public List<RelationshipDto> getAllRelationships(){
        return relationshipRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

//    Update Relationship record
    @Transactional
    public RelationshipDto updateRelationship(Long id, RelationshipDto relationshipDto){
        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relationship not found with id: " + id));

        relationship.setName(relationshipDto.getName());
        relationship.setWebsite(relationshipDto.getWebsite());
        relationship.setEmail(relationshipDto.getEmail());

        Relationship updated = relationshipRepository.save(relationship);
        return mapToDto(updated);
}

//    Delete by Id
    @Transactional
    public void deleteRelationship(Long id){
        relationshipRepository.deleteById(id);
    }



    private RelationshipDto mapToDto(Relationship relationship){
        return new RelationshipDto.Builder()
                .id(relationship.getId())
                .name(relationship.getName())
                .website(relationship.getWebsite())
                .email(relationship.getEmail())
                .build();
    }
}
