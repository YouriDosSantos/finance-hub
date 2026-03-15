package com.finance.hub.dataTransfer;

import com.finance.hub.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private boolean mustChangePassword;

    private final List<String> roles = new ArrayList<>();

    public UserDto(User entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        mustChangePassword = entity.isMustChangePassword();
        for(GrantedAuthority role: entity.getAuthorities()) {
            roles.add(role.getAuthority());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }
}
