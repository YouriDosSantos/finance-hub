package com.finance.hub.controller;

import com.finance.hub.dataTransfer.RelationshipDto;
import com.finance.hub.service.RelationshipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<RelationshipDto> createRelationship(@RequestBody RelationshipDto relationshipDto){
        RelationshipDto created = relationshipService.createRelationship(relationshipDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    Get Relationship by ID
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<RelationshipDto> getRelationshipById(@PathVariable Long id){
        RelationshipDto relationshipDto = relationshipService.getRelationshipById(id);
        return ResponseEntity.ok(relationshipDto);
    }

////    Get all Relationships
//    @GetMapping
//    public ResponseEntity<List<RelationshipDto>> getAllRelationships(){
//        return ResponseEntity.ok(relationshipService.getAllRelationships());
//    }

//  Change for Pagination
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping
    public ResponseEntity<Page<RelationshipDto>> getAllRelationships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {

        Sort sort = direction .equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RelationshipDto> result = relationshipService.getAllRelationships(search, pageable);
        return ResponseEntity.ok(result);

    }

//   Update a Relationship
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RelationshipDto> updateRelationship(@PathVariable Long id, @RequestBody RelationshipDto relationshipDto){
        RelationshipDto update = relationshipService.updateRelationship(id, relationshipDto);
        return ResponseEntity.ok(update);
    }

//    Delete a Relationship
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<RelationshipDto> deleteRelationship(@PathVariable Long id){
        relationshipService.deleteRelationship(id);
        return ResponseEntity.noContent().build();
    }
}
