package com.finance.hub.repository;

import com.finance.hub.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

//    Find All contacts by last name
    List<Contact> findByLastName(String lastName);


//    FInd all contacts belonging to a Relationship
    List<Contact> findByRelationshipId(Long relationshipId);


//    Find All Contacts by job title
    List<Contact> findByJobTitle(String jobTitle);


//    If Email is unique per contact, return Optional
    Optional<Contact> findByEmail(String Email);

    // Change for Pageable/Search
    Page<Contact> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrJobTitleContainingIgnoreCase(
            String firstName, String lastName, String email, String jobTitle, Pageable pageable
    );


//    THE BELOW COULD BE USED IN A JPA/RAW SQL MIX WITH ANNOTATION

    /*@Query(value = "SELECT * FROM contacts WHERE last_name = :lastName", nativeQuery = true)
    List<Contact> findByLastName(@Param("lastName") String lastName);

    @Query(value = "SELECT * FROM contacts WHERE relationship_id = :relationshipId", nativeQuery = true)
    List<Contact> findByRelationshipId(@Param("relationshipId") Long relationshipId);

    @Query(value = "SELECT * FROM contacts WHERE job_title = :jobTitle", nativeQuery = true)
    List<Contact> findByJobTitle(@Param("jobTitle") String jobTitle);*/


}




