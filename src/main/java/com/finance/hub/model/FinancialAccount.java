package com.finance.hub.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class FinancialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;


    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedOn;

    public FinancialAccount(){

    }

    public FinancialAccount(Long id, String accountName, String accountNumber, String accountType, BigDecimal balance, Relationship relationship) {
        this.id = id;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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
