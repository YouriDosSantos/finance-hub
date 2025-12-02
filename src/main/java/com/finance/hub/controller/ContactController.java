package com.finance.hub.controller;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.service.ContactService;
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
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

//    Create
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto){
        ContactDto created = contactService.createContact(contactDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    Get by ID
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id){
        ContactDto contact = contactService.getContactById(id);

        return ResponseEntity.ok(contact);
    }

////    Get All
//    @GetMapping
//    public ResponseEntity<List<ContactDto>> getAllContacts(){
//        return ResponseEntity.ok(contactService.getAllContacts());
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping
    public ResponseEntity<Page<ContactDto>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search
    ){
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ContactDto> result = contactService.getAllContacts(search, pageable);
        return ResponseEntity.ok(result);
    }

//    Update
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(
            @PathVariable Long id,
            @RequestBody ContactDto contactDto
    ) {
        ContactDto updated = contactService.updateContact(id, contactDto);
        return ResponseEntity.ok(updated);
    }

//    Delete
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id){
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build(); //204: successful delete, no content
    }
}
