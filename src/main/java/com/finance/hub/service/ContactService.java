package com.finance.hub.service;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.jdbcRepo.ContactJdbcRepository;
import com.finance.hub.model.Contact;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.ContactRepository;
import com.finance.hub.repository.RelationshipRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {

//    private final ContactRepository contactRepository;
    private final ContactJdbcRepository contactJdbcRepository;
    private final RelationshipRepository relationshipRepository;

    public ContactService(ContactJdbcRepository contactJdbcRepository, RelationshipRepository relationshipRepository) {
        this.contactJdbcRepository = contactJdbcRepository;
        this.relationshipRepository = relationshipRepository;
    }

    //Cache Warming
    @PostConstruct
    public void warmCaches() {
        getCachedContactList("", 10, 0, "id", "ASC");
        getCachedContactCount("");
    }

    //    Create a New Contact -> One DB write (save). Transaction ensures rollback if relationship not found or save fails.
    //Added Redis/Cache annotation. List cache becomes stale. CREATE
    @Transactional
    @CacheEvict(value = {"contacts_list", "contacts_count"}, allEntries = true)
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

        Contact saved = contactJdbcRepository.save(contact);

        //No @CachePut here because ID is generated AFTER save
        return mapToDto(saved);
    }

    //    Find Contact by ID -> Read-only transaction for performance optimization
    // ADDED cacheable. READ BY ID
    @Transactional(readOnly = true)
    @Cacheable(value = "contacts", key = "#id")
    public ContactDto getContactById(Long id){
        Contact contact = contactJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        return mapToDto(contact);
    }

    //ADDED Cacheable. LIST + SEARCH
    @Transactional(readOnly = true)
    public Page<ContactDto> getAllContacts(String search, int limit, int offset, String sortBy, String direction) {
        //1. Load cached list
        List<ContactDto> cachedList = getCachedContactList(search, limit, offset, sortBy, direction);

        //2. Load cached count
        Long total = getCachedContactCount(search);

        //3. Build page object manually
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return  new PageImpl<>(cachedList, pageRequest, total);
    }


    @Cacheable(value = "contacts_list", keyGenerator = "hashKeyGenerator")
    public List<ContactDto> getCachedContactList(String search, int limit, int offset, String sortBy, String direction) {

        List<Contact> contacts;

        if (search == null || search.isBlank()) {
            contacts = contactJdbcRepository.findAll(limit, offset, sortBy, direction);
        } else {
            contacts = contactJdbcRepository.searchContacts(search, limit, offset, sortBy, direction);
        }

        return contacts.stream().map(this::mapToDto).toList();
    }


    @Cacheable(value = "contacts_count", keyGenerator = "hashKeyGenerator" )
    public Long getCachedContactCount(String search) {
        return contactJdbcRepository.countContacts(search);
    }


    //    Update a Contact -> Update modifies + saves. Needs a transaction for atomicity.
    //UPDATE CACHE
    @Transactional
    @CachePut(value = "contacts", key = "#id") //update single cached contact
    @CacheEvict(value = {"contacts_list", "contacts_count"}, allEntries = true) //list cache becomes stale
    public ContactDto updateContact(Long id, ContactDto contactDto){
        Contact contact = contactJdbcRepository.findById(id)
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

        Contact updated = contactJdbcRepository.save(contact);
        return mapToDto(updated);
    }

    //    Delete a contact -> One DB delete. Wraps delete in a transaction.
    //DELETE Cacheable. Added @caching and evict because java does not let multiple annotation be together such as CacheEvict back to back without the @Caching ()
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "contacts", key = "#id"), //remove single cached contact
                    @CacheEvict(value = {"contacts_list", "contacts_count"}, allEntries = true) //list cache becomes stale
            }
    )
    public void deleteContact(Long id){
        if(!contactJdbcRepository.existsById(id)){
            throw new EntityNotFoundException("Contact not found with id: " + id);
        }

        contactJdbcRepository.deleteById(id);
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
                contact.getRelationship() != null ? contact.getRelationship().getId() : null,
                contact.getRelationship() != null ? contact.getRelationship().getName() : null
        );
    }
}