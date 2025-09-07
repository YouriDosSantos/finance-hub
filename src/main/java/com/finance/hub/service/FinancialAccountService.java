package com.finance.hub.service;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.EntityNotFoundException;
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

        if(financialAccountDto.getRelationshipId() == null){
            throw new BadRequestException(("Relationship ID is required for creating a financial account."));
        }

        Relationship relationship = relationshipRepository.findById(financialAccountDto.getRelationshipId())
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found"));

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
    public FinancialAccountDto getAccountById(Long id) {
        FinancialAccount financialAccount = financialAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial Account not found with id: " + id));

        return mapToDto(financialAccount);
    }

//    Get All FinancialAccounts
    @Transactional(readOnly = true)
    public List<FinancialAccountDto> getAllFinancialAccounts(){
        return financialAccountRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

//   Update Financial Account
    @Transactional
    public FinancialAccountDto updateFinancialAccount(Long id, FinancialAccountDto financialAccountDto){
        FinancialAccount financialAccount = financialAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial Account not found with id: " + id));

        financialAccount.setAccountName(financialAccountDto.getAccountName());
        financialAccount.setAccountNumber(financialAccountDto.getAccountNumber());
        financialAccount.setAccountType(financialAccountDto.getAccountType());
        financialAccount.setBalance(financialAccountDto.getBalance());

        if(!financialAccount.getRelationship().getId().equals(financialAccountDto.getRelationshipId())) {
            Relationship relationship = relationshipRepository.findById(financialAccountDto.getRelationshipId())
                    .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + financialAccountDto.getRelationshipId()));
            financialAccount.setRelationship(relationship);
        }

        FinancialAccount updated = financialAccountRepository.save(financialAccount);
        return mapToDto(updated);
    }


//    Delete
    @Transactional
    public void deleteFinancialAccount(Long id){
        if(!financialAccountRepository.existsById(id)){
            throw new EntityNotFoundException("Financial Account not found with id: " + id);
        }

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

