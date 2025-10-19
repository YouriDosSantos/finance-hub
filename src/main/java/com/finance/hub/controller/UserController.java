package com.finance.hub.controller;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.dataTransfer.UserDto;
import com.finance.hub.service.ContactService;
import com.finance.hub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //    Get by ID

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping("/me")
    public ResponseEntity<UserDto> findMe(){
        UserDto userDto = userService.getMe();

        return ResponseEntity.ok(userDto);
    }

////    Create
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PostMapping
//    public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto){
//        ContactDto created = contactService.createContact(contactDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }


//
////    Get All
//    @GetMapping
//    public ResponseEntity<List<ContactDto>> getAllContacts(){
//        return ResponseEntity.ok(contactService.getAllContacts());
//    }
//
////    Update
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<ContactDto> updateContact(
//            @PathVariable Long id,
//            @RequestBody ContactDto contactDto
//    ) {
//        ContactDto updated = contactService.updateContact(id, contactDto);
//        return ResponseEntity.ok(updated);
//    }
//
////    Delete
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteContact(@PathVariable Long id){
//        contactService.deleteContact(id);
//        return ResponseEntity.noContent().build(); //204: successful delete, no content
//    }
}
