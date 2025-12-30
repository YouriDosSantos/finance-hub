package com.finance.hub.service;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.jdbcRepo.FinancialAccountJdbcRepository;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.model.Relationship;
import com.finance.hub.repository.FinancialAccountRepository;
import com.finance.hub.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FinancialAccountService {

    private final FinancialAccountJdbcRepository financialAccountJdbcRepository;
    private final RelationshipRepository relationshipRepository;

    public FinancialAccountService(FinancialAccountJdbcRepository financialAccountJdbcRepository, RelationshipRepository relationshipRepository) {
        this.financialAccountJdbcRepository = financialAccountJdbcRepository;
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

        FinancialAccount saved = financialAccountJdbcRepository.save(financialAccount);
        return mapToDto(saved);
    }

    //    Get Account By ID
    @Transactional(readOnly = true)
    public FinancialAccountDto getAccountById(Long id) {
        FinancialAccount financialAccount = financialAccountJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial Account not found with id: " + id));

        return mapToDto(financialAccount);
    }

//  Changes for Pagination
    @Transactional(readOnly = true)
    public Page<FinancialAccountDto> getAllFinancialAccounts(String search, int limit, int offset, String sortBy, String direction) {
        List<FinancialAccount> financialAccounts;

        if(search == null || search.isBlank()){
            financialAccounts = financialAccountJdbcRepository.findAll(limit, offset, sortBy, direction);
        } else {
            financialAccounts = financialAccountJdbcRepository
                    .searchFinancialAccounts(
                            search, limit, offset, sortBy, direction);
        }

        List<FinancialAccountDto> dtos = financialAccounts.stream().map(this::mapToDto).toList();
        Long total = financialAccountJdbcRepository.countFinancialAccounts(search);

        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return new PageImpl<>(dtos, pageRequest, total);
    }


//   Update Financial Account
    @Transactional
    public FinancialAccountDto updateFinancialAccount(Long id, FinancialAccountDto financialAccountDto){
        FinancialAccount financialAccount = financialAccountJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial Account not found with id: " + id));

        financialAccount.setAccountName(financialAccountDto.getAccountName());
        financialAccount.setAccountNumber(financialAccountDto.getAccountNumber());
        financialAccount.setAccountType(financialAccountDto.getAccountType());
        financialAccount.setBalance(financialAccountDto.getBalance());

        if(financialAccountDto.getRelationshipId() != null  && !financialAccount.getRelationship().getId().equals(financialAccountDto.getRelationshipId())) {
            Relationship relationship = relationshipRepository.findById(financialAccountDto.getRelationshipId())
                    .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + financialAccountDto.getRelationshipId()));
            financialAccount.setRelationship(relationship);
        }

        FinancialAccount updated = financialAccountJdbcRepository.save(financialAccount);
        return mapToDto(updated);
    }


//    Delete
    @Transactional
    public void deleteFinancialAccount(Long id){
        if(!financialAccountJdbcRepository.existsById(id)){
            throw new EntityNotFoundException("Financial Account not found with id: " + id);
        }

        financialAccountJdbcRepository.deleteById(id);
    }

//Mapping Helper
    private FinancialAccountDto mapToDto(FinancialAccount financialAccount) {
        return new FinancialAccountDto(
                financialAccount.getId(),
                financialAccount.getAccountName(),
                financialAccount.getAccountNumber(),
                financialAccount.getAccountType(),
                financialAccount.getBalance(),
                financialAccount.getRelationship() != null ? financialAccount.getRelationship().getId() : null,
                financialAccount.getRelationship() != null ? financialAccount.getRelationship().getName() : null
        );
    }
}

