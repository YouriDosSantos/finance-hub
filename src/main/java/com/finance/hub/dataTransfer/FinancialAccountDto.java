package com.finance.hub.dataTransfer;

import com.finance.hub.model.Relationship;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialAccountDto {

    private final Long id;
    private final String accountName;
    private final String accountNumber;
    private final String accountType;
    private final BigDecimal balance;
    private final Long relationshipId;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedOn;

    private FinancialAccountDto(Builder builder){
            this.id = builder.id;
            this.accountName = builder.accountName;
            this.accountNumber = builder.accountNumber;
            this.accountType = builder.accountType;
            this.balance = builder.balance;
            this.relationshipId = builder.relationshipId;
            this.createdAt = builder.createdAt;
            this.modifiedOn = builder.modifiedOn;
    }

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
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
        private String accountName;
        private String accountNumber;
        private String accountType;
        private BigDecimal balance;
        private Long relationshipId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedOn;

        public FinancialAccountDto.Builder id(Long id){
            this.id = id;
            return this;
        }

        public FinancialAccountDto.Builder accountName(String accountName){
            this.accountName = accountName;
            return this;
        }

        public FinancialAccountDto.Builder accountNumber(String accountNumber){
            this.accountNumber = accountNumber;
            return this;
        }

        public FinancialAccountDto.Builder accountType(String accountType){
            this.accountType = accountType;
            return this;
        }

        public FinancialAccountDto.Builder balance(BigDecimal balance){
            this.balance = balance;
            return this;
        }

        public FinancialAccountDto.Builder relationshipId(Long relationshipId){
            this.relationshipId = relationshipId;
            return this;
        }

        public FinancialAccountDto.Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public FinancialAccountDto.Builder modifiedOn(LocalDateTime modifiedOn){
            this.modifiedOn = modifiedOn;
            return this;
        }

        public FinancialAccountDto build(){
            return new FinancialAccountDto(this);
        }



    }
}
