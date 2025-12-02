package com.finance.hub.controller;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.service.FinancialAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial-accounts")
public class FinancialAccountController {

    public final FinancialAccountService financialAccountService;

    public FinancialAccountController(FinancialAccountService financialAccountService) {
        this.financialAccountService = financialAccountService;
    }

//    Create Account
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<FinancialAccountDto> createFinancialAccount(@RequestBody FinancialAccountDto financialAccountDto){
        FinancialAccountDto created = financialAccountService.createFinancialAccount(financialAccountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    Get Account by ID
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<FinancialAccountDto> getFinancialAccountById(@PathVariable Long id){
        FinancialAccountDto financialAccountDto = financialAccountService.getAccountById(id);
        return ResponseEntity.ok(financialAccountDto);
    }

////    Get All Accounts
//    @GetMapping
//    public ResponseEntity<List<FinancialAccountDto>> getAllFinancialAccounts(){
//        return ResponseEntity.ok(financialAccountService.getAllFinancialAccounts());
//    }

//  Change for Pagination
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping
    public ResponseEntity<Page<FinancialAccountDto>> getAllFinancialAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FinancialAccountDto> result = financialAccountService.getAllFinancialAccounts(search, pageable);
        return ResponseEntity.ok(result);
    }



//    Update account
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FinancialAccountDto> updateFinancialAccount(@PathVariable Long id, @RequestBody FinancialAccountDto financialAccountDto){
        FinancialAccountDto updated = financialAccountService.updateFinancialAccount(id, financialAccountDto);
        return ResponseEntity.ok(updated);
    }

//    Delete Account
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialAccount(@PathVariable Long id){
        financialAccountService.deleteFinancialAccount(id);
        return ResponseEntity.noContent().build(); //204: successful delete, no content
    }
}
