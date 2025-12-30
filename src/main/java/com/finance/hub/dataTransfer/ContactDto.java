
package com.finance.hub.dataTransfer;

import com.finance.hub.model.Relationship;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.lang.module.ModuleDescriptor;
import java.time.LocalDateTime;

public class ContactDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private Long relationshipId;
    private String relationshipName;

    public ContactDto(){}


    public ContactDto(Long id, String firstName, String lastName, String email, String phone, String jobTitle, Long relationshipId, String relationshipName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.relationshipId = relationshipId;
        this.relationshipName = relationshipName;

    }

    public Long getId() {
        return id;
    }

    public Long getRelationshipId() {
        return relationshipId;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
