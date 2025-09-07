package com.finance.hub.controller;

import com.finance.hub.dataTransfer.RelationshipDto;
import com.finance.hub.service.RelationshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    public final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

//    Create Relationship
    @PostMapping
    public ResponseEntity<RelationshipDto> createRelationship(@RequestBody RelationshipDto relationshipDto){
        RelationshipDto created = relationshipService.createRelationship(relationshipDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    Get Relationship by ID
    @GetMapping("/{id}")
    public ResponseEntity<RelationshipDto> getRelationshipById(@PathVariable Long id){
        RelationshipDto relationshipDto = relationshipService.getRelationshipById(id);
        return ResponseEntity.ok(relationshipDto);
    }

//    Get all Relationships
    @GetMapping
    public ResponseEntity<List<RelationshipDto>> getAllRelationships(){
        return ResponseEntity.ok(relationshipService.getAllRelationships());
    }

//   Update a Relationship
    @PutMapping("/{id}")
    public ResponseEntity<RelationshipDto> updateRelationship(@PathVariable Long id, @RequestBody RelationshipDto relationshipDto){
        RelationshipDto update = relationshipService.updateRelationship(id, relationshipDto);
        return ResponseEntity.ok(update);
    }

//    Delete a Relationship
    @DeleteMapping("/{id}")
    public ResponseEntity<RelationshipDto> deleteRelationship(@PathVariable Long id){
        relationshipService.deleteRelationship(id);
        return ResponseEntity.noContent().build();
    }
}
