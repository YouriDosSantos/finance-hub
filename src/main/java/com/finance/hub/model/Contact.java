package com.finance.hub.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Contact {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;

    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedOn;

    public Contact(){

    }

    public Contact(Long id, String firstName, String lastName, String email, String phone, String jobTitle, Relationship relationship) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.relationship = relationship;
    }

    @PrePersist
    protected  void onCreate(){
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onModified(){
        modifiedOn = LocalDateTime.now();
    }



    public Long getId() {
        return id;
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

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}
