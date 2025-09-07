package com.finance.hub.controller;

import com.finance.hub.dataTransfer.FinancialAccountDto;
import com.finance.hub.model.FinancialAccount;
import com.finance.hub.service.FinancialAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financialAccounts")
public class FinancialAccountController {

    public final FinancialAccountService financialAccountService;

    public FinancialAccountController(FinancialAccountService financialAccountService) {
        this.financialAccountService = financialAccountService;
    }

//    Create Account
    @PostMapping
    public ResponseEntity<FinancialAccountDto> createFinancialAccount(@RequestBody FinancialAccountDto financialAccountDto){
        FinancialAccountDto created = financialAccountService.createFinancialAccount(financialAccountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    Get Account by ID
    @GetMapping("/{id}")
    public ResponseEntity<FinancialAccountDto> getFinancialAccountById(@PathVariable Long id){
        FinancialAccountDto financialAccountDto = financialAccountService.getAccountById(id);
        return ResponseEntity.ok(financialAccountDto);
    }

//    Get All Accounts
    @GetMapping
    public ResponseEntity<List<FinancialAccountDto>> getAllFinancialAccounts(){
        return ResponseEntity.ok(financialAccountService.getAllFinancialAccounts());
    }

//    Update account
    @PutMapping("/{id}")
    public ResponseEntity<FinancialAccountDto> updateFinancialAccount(@PathVariable Long id, @RequestBody FinancialAccountDto financialAccountDto){
        FinancialAccountDto updated = financialAccountService.updateFinancialAccount(id, financialAccountDto);
        return ResponseEntity.ok(updated);
    }

//    Delete Account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialAccount(@PathVariable Long id){
        financialAccountService.deleteFinancialAccount(id);
        return ResponseEntity.noContent().build(); //204: successful delete, no content
    }
}
