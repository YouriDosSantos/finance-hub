package com.finance.hub.controller;

import com.finance.hub.dataTransfer.AdminUserDto;
import com.finance.hub.dataTransfer.UserDto;
import com.finance.hub.model.User;
import com.finance.hub.record.UpdateEmailRequest;
import com.finance.hub.record.UpdateNameRequest;
import com.finance.hub.record.UpdateRolesRequest;
import com.finance.hub.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public List<AdminUserDto> listUsers() {
        return adminUserService.findAllUsers();
    }

    @PutMapping("/users/{id}/name")
    public void updateName(@PathVariable Long id, @Valid @RequestBody UpdateNameRequest request) {
        adminUserService.updateUserName(id, request.name());
    }

    @PutMapping("/users/{id}/email")
    public void updateEmail(@PathVariable Long id, @Valid @RequestBody UpdateEmailRequest request) {
        adminUserService.updateUserEmail(id, request.email());
    }

    @PutMapping("/users/{id}/roles")
    public void updateRoles(@PathVariable Long id, @Valid @RequestBody UpdateRolesRequest request) {
        adminUserService.updateUserRoles(id, request.roles());
    }

    @GetMapping("/roles")
    public List<String> getAllRoles() {
        return adminUserService.getAllRoles();
    }

}
