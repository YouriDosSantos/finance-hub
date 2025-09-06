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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedOn;

    private ContactDto(Builder builder){
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.jobTitle = builder.jobTitle;
        this.relationshipId = builder.relationshipId;
        this.createdAt = builder.createdAt;
        this.modifiedOn = builder.modifiedOn;
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Long getRelationshipId() {
        return relationshipId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public static class Builder{
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String jobTitle;
        private Long relationshipId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedOn;

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder phone(String phone){
            this.phone = phone;
            return this;
        }

        public Builder jobTitle(String jobTitle){
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder relationshipId(Long relationshipId){
            this.relationshipId = relationshipId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder modifiedOn(LocalDateTime modifiedOn){
            this.modifiedOn = modifiedOn;
            return this;
        }

        public ContactDto build(){
            return new ContactDto(this);
        }

    }
}
