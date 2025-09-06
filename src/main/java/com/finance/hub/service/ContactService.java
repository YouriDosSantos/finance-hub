package com.finance.hub.service;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.model.Contact;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.ContactRepository;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        Relationship relationship = relationshipRepository.findById(contactDto.getRelationshipId())
                .orElseThrow(() -> new IllegalArgumentException("Relationship not found"));

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
    public Optional<ContactDto> getContactById(Long id){
        return contactRepository.findById(id).map(this::mapToDto);
    }

//    Find All Contacts
    @Transactional(readOnly = true)
    public List<ContactDto> getAllContacts(){
        return contactRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

//    Update a Contact -> Update modifies + saves. Needs a transaction for atomicity.
    @Transactional
    public ContactDto updateContact(Long id, ContactDto contactDto){
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + id));

        //Update fields
        contact.setFirstName(contactDto.getFirstName());
        contact.setLastName(contactDto.getLastName());
        contact.setEmail(contactDto.getEmail());
        contact.setPhone(contactDto.getPhone());
        contact.setJobTitle(contactDto.getJobTitle());

        //update relationhip if changed
        if(!contact.getRelationship().getId().equals(contactDto.getRelationshipId())){
            Relationship relationship = relationshipRepository.findById(contactDto.getRelationshipId())
                    .orElseThrow(() -> new IllegalArgumentException("Relationship not found"));
            contact.setRelationship(relationship);
        }

        Contact updated = contactRepository.save(contact);
        return mapToDto(updated);
    }

//    Delete a contact -> One DB delete. Wraps delete in a transaction.
    @Transactional
    public void deleteContact(Long id){
        contactRepository.deleteById(id);
    }


//    Mapping Helper
    private ContactDto mapToDto(Contact contact){
        return new ContactDto.Builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .jobTitle(contact.getJobTitle())
                .relationshipId(contact.getRelationship().getId())
                .build();
    }
}
