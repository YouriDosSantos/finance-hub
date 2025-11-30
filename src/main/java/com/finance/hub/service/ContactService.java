package com.finance.hub.service;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.model.Contact;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.ContactRepository;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final RelationshipRepository relationshipRepository;

    public ContactService(ContactRepository contactRepository, RelationshipRepository relationshipRepository) {
        this.contactRepository = contactRepository;
        this.relationshipRepository = relationshipRepository;
    }

    //    Create a New Contact -> One DB write (save). Transaction ensures rollback if relationship not found or save fails.
    @Transactional
    public ContactDto createContact(ContactDto contactDto){

        if(contactDto.getRelationshipId() == null){
            throw new BadRequestException("Relationship ID is required for creating a contact.");
        }

        Relationship relationship = relationshipRepository.findById(contactDto.getRelationshipId())
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with ID: " + contactDto.getRelationshipId()));

        Contact contact = new Contact(
                contactDto.getId(),
                contactDto.getFirstName(),
                contactDto.getLastName(),
                contactDto.getEmail(),
                contactDto.getPhone(),
                contactDto.getJobTitle(),
                relationship
        );

        Contact saved = contactRepository.save(contact);
        return mapToDto(saved);
    }

    //    Find Contact by ID -> Read-only transaction for performance optimization
    @Transactional(readOnly = true)
    public ContactDto getContactById(Long id){
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        return mapToDto(contact);
    }

//    //    Find All Contacts
//    @Transactional(readOnly = true)
//    public List<ContactDto> getAllContacts(){
//        return contactRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
//    }

//    //Change for pageable
//    @Transactional(readOnly = true)
//    public Page<ContactDto> getAllContacts(Pageable pageable){
//        return contactRepository.findAll(pageable).map(this::mapToDto);
//    }

    @Transactional(readOnly = true)
    public Page<ContactDto> getAllContacts(String search, Pageable pageable) {
        Page<Contact> contacts;

        if(search == null || search.isBlank()) {
            contacts = contactRepository.findAll(pageable);
        } else {
            contacts = contactRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrJobTitleContainingIgnoreCase(
                            search, search, search, search, pageable
                    );
        }
        return contacts.map(this::mapToDto);
    }


    //    Update a Contact -> Update modifies + saves. Needs a transaction for atomicity.
    @Transactional
    public ContactDto updateContact(Long id, ContactDto contactDto){
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));

        //Update fields
        contact.setFirstName(contactDto.getFirstName());
        contact.setLastName(contactDto.getLastName());
        contact.setEmail(contactDto.getEmail());
        contact.setPhone(contactDto.getPhone());
        contact.setJobTitle(contactDto.getJobTitle());

        //update relationship if changed
        if(contactDto.getRelationshipId() != null && !contact.getRelationship().getId().equals(contactDto.getRelationshipId())){
            Relationship relationship = relationshipRepository.findById(contactDto.getRelationshipId())
                    .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + contactDto.getRelationshipId()));
            contact.setRelationship(relationship);
        }

        Contact updated = contactRepository.save(contact);
        return mapToDto(updated);
    }

    //    Delete a contact -> One DB delete. Wraps delete in a transaction.
    @Transactional
    public void deleteContact(Long id){
        if(!contactRepository.existsById(id)){
            throw new EntityNotFoundException("Contact not found with id: " + id);
        }

        contactRepository.deleteById(id);
    }


    //    Mapping Helper
    private ContactDto mapToDto(Contact contact){
        return new ContactDto(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getJobTitle(),
                contact.getRelationship() != null ? contact.getRelationship().getId() : null
        );
    }
}