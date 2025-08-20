package com.finance.hub.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String AccountType;
    private BigDecimal balance;


    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedOn;

    private FinancialAccount(){

    }

    public FinancialAccount(Long id, String accountNumber, String accountType, BigDecimal balance, Relationship relationship) {
        this.id = id;
        this.accountNumber = accountNumber;
        AccountType = accountType;
        this.balance = balance;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}
