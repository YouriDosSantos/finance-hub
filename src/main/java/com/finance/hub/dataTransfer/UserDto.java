package com.finance.hub.dataTransfer;

import com.finance.hub.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
    private Long id;
    private String name;
    private String email;

    private final List<String> roles = new ArrayList<>();

    public UserDto(User entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
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
}
