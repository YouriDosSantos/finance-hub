package com.finance.hub.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Relationship {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;
    private String website;
    private String Email;
    private String phone;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialAccount> accounts = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedOn;

    public Relationship(){

    }

    public Relationship(Long id, String name, String website, String email, String phone) {
        this.id = id;
        this.name = name;
        this.website = website;
        Email = email;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<FinancialAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<FinancialAccount> accounts) {
        this.accounts = accounts;
    }
}
