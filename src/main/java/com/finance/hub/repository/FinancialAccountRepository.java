package com.finance.hub.repository;

import com.finance.hub.model.FinancialAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {

//    Find Accounts by type
    List<FinancialAccount> findByAccountType(String accountType);

//    Find all accounts by relationship id
    List<FinancialAccount> findByRelationshipId(Long relationshipId);

//    Find accounts by accountNumber
    Optional<FinancialAccount> findByAccountNumber(String accountNumber);

//    find Accounts with a balance greater than a given amount
    List<FinancialAccount> findByBalanceGreaterThan(BigDecimal amount);

//    Change for Pagination
    Page<FinancialAccount> findByAccountNameContainingIgnoreCaseOrAccountNumberContainingIgnoreCaseOrAccountTypeContainingIgnoreCase(
            String accountName, String accountNumber, String accountType, Pageable pageable
    );
































//    THE BELOW COULD BE USED IN A JPA/RAW SQL MIX WITH ANNOTATION

//    @Query(value = "SELECT * FROM financial_accounts WHERE account_type = :accountType", nativeQuery = true)
//    List<FinancialAccount> findByAccountType(@Param("accountType") String accountType);
//
//    @Query(value = "SELECT * FROM financial_accounts WHERE relationship_id = :relationshipId", nativeQuery = true)
//    List<FinancialAccount> findByRelationshipId(@Param("relationshipId") Long relationshipId);
//
//    @Query(value = "SELECT * FROM financial_accounts WHERE balance > :amount", nativeQuery = true)
//    List<FinancialAccount> findByBalanceGreaterThan(@Param("amount")BigDecimal amount);




}
