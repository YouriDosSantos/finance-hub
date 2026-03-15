package com.finance.hub.service;

import com.finance.hub.dataTransfer.AdminUserDto;
import com.finance.hub.dataTransfer.UserDto;
import com.finance.hub.exception.BadRequestException;
import com.finance.hub.exception.EntityNotFoundException;
import com.finance.hub.jdbcRepo.RoleJdbcRepository;
import com.finance.hub.jdbcRepo.UserJdbcRepository;
import com.finance.hub.model.Role;
import com.finance.hub.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AdminUserService {

    private final UserJdbcRepository userJdbcRepository;
    private final RoleJdbcRepository roleJdbcRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public AdminUserService(UserJdbcRepository userJdbcRepository, RoleJdbcRepository roleJdbcRepository, PasswordEncoder passwordEncoder) {
        this.userJdbcRepository = userJdbcRepository;
        this.roleJdbcRepository = roleJdbcRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminUserDto> findAllUsers() {
        return userJdbcRepository.findAllUsers()
                .stream()
                .map(user -> new AdminUserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())).toList();
    }

    @Transactional
    public void updateUserName(Long id, String name) {
        userJdbcRepository.updateUserName(id, name);
    }

    @Transactional
    public void updateUserEmail(Long id, String email) {
        userJdbcRepository.findByEmail(email).ifPresent(existingUser -> {
            if(!existingUser.getId().equals(id)) {
                throw new BadRequestException("Email already exists");
            }
        });

        userJdbcRepository.updateUserEmail(id, email);
    }

    @Transactional
    public void updateUserRoles(Long id, List<String> roleAuthorities) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();

        String currentUserEmail = jwt.getClaim("username");
//        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userJdbcRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        // Prevent removing all roles from yourself
        if (currentUser.getId().equals(id) && roleAuthorities.isEmpty()) {
            throw new BadRequestException("You cannot remove all roles from yourself");
        }

        // Prevent removing your own admin role
        if (currentUser.getId().equals(id) && !roleAuthorities.contains("ROLE_ADMIN")) {
            throw new BadRequestException("You cannot remove your own admin role");
        }

        // Validate roles exist
        List<Role> roles = roleAuthorities.stream()
                .map(authority -> {
                    Role role = roleJdbcRepository.findByAuthority(authority);
                    if (role == null) {
                        throw new BadRequestException("Invalid role: " + authority);
                    }
                    return role;
                })
                .toList();

        userJdbcRepository.updateUserRoles(id, roles);
    }


    public List<String> getAllRoles() {
        return roleJdbcRepository.findAll()
                .stream()
                .map(Role::getAuthority)
                .toList();
    }

    @Transactional
    public String resetPassword(Long id) {
        User user = userJdbcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String tempPassword = generateTempPassword(10);
        String encoded = passwordEncoder.encode(tempPassword);

        userJdbcRepository.updatePasswordAndFlag(id, encoded, true);

        System.out.println("TEMP PASSWORD GENERATED: [" + tempPassword + "]");

        return tempPassword;
    }

    private String generateTempPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }

        return sb.toString();
    }




}
