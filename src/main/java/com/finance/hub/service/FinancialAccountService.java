package com.finance.hub.service;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.FinancialAccountRepository;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final RelationshipRepository relationshipRepository;

    public FinancialAccountService(FinancialAccountRepository financialAccountRepository, RelationshipRepository relationshipRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.relationshipRepository = relationshipRepository;
    }

    //    Create a Financial Account
    @Transactional
    public FinancialAccountDto createFinancialAccount(FinancialAccountDto financialAccountDto) {
        Relationship relationship = relationshipRepository.findById(financialAccountDto.getRelationshipId())
                .orElseThrow(() -> new IllegalArgumentException("Relationship not found"));

        FinancialAccount financialAccount = new FinancialAccount(
                financialAccountDto.getId(),
                financialAccountDto.getAccountName(),
                financialAccountDto.getAccountNumber(),
                financialAccountDto.getAccountType(),
                financialAccountDto.getBalance(),
                relationship
        );

        FinancialAccount saved = financialAccountRepository.save(financialAccount);
        return mapToDto(saved);
    }

    //    Get Account By ID
    @Transactional(readOnly = true)
    public Optional<FinancialAccountDto> getAccountById(Long id) {
        return financialAccountRepository.findById(id).map(this::mapToDto);
    }

//    Get All FinancialAccounts
    @Transactional(readOnly = true)
    public List<FinancialAccountDto> getAllFinancialAccounts(){
        return financialAccountRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

//   Update Financial Account
    @Transactional
    public FinancialAccountDto updateFinancialAccount(Long id, FinancialAccountDto financialAccountDto){
        FinancialAccount account = financialAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Financial Account not found with id: " + id));

        account.setAccountName(financialAccountDto.getAccountName());
        account.setAccountNumber(financialAccountDto.getAccountNumber());
        account.setAccountType(financialAccountDto.getAccountType());
        account.setBalance(financialAccountDto.getBalance());

        if(!account.getRelationship().getId().equals(financialAccountDto.getRelationshipId())) {
            Relationship relationship = relationshipRepository.findById(financialAccountDto.getRelationshipId())
                    .orElseThrow(() -> new IllegalArgumentException("Relationship not found"));
            account.setRelationship(relationship);
        }

        FinancialAccount updated = financialAccountRepository.save(account);
        return mapToDto(updated);
    }


//    Delete
    @Transactional
    public void deleteFinancialAccount(Long id){
        financialAccountRepository.deleteById(id);
    }

//Mapping Helper
    private FinancialAccountDto mapToDto(FinancialAccount financialAccount) {
        return new FinancialAccountDto.Builder()
                .id(financialAccount.getId())
                .accountName(financialAccount.getAccountName())
                .accountNumber(financialAccount.getAccountNumber())
                .accountType(financialAccount.getAccountType())
                .balance(financialAccount.getBalance())
                .relationshipId(financialAccount.getRelationship().getId())
                .build();
    }
}

