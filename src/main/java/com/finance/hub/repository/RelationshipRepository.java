package com.finance.hub.repository;

import com.finance.hub.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    //Custom Query Examples

    List<Relationship> findByNameContainingIgnoreCase(String name);


    Optional<Relationship> findByEmail(String email);

    //    THE BELOW COULD BE USED IN A JPA/RAW SQL MIX WITH ANNOTATION

//    @Query(value = "SELECT * FROM relationships WHERE LOWER(name) like LOWER(CONCAT('%', :name, '%'))", nativeQuery = true)
//    List<Relationship> findByNameContainingIgnoreCase(@Param("name") String name);
//
//    @Query(value = "SELECT * FROM relationships WHERE email = :email", nativeQuery = true)
//    List<Relationship> findByEmail(@Param("email") String email);
}
