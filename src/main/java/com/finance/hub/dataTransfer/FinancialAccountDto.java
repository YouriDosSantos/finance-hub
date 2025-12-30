package com.finance.hub.dataTransfer;

import com.finance.hub.model.Relationship;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialAccountDto {

    private Long id;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Long relationshipId;
    private String relationshipName;

    public FinancialAccountDto(){}

    public FinancialAccountDto(Long id, String accountName, String accountNumber, String accountType, BigDecimal balance, Long relationshipId, String relationshipName) {
        this.id = id;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
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
}


