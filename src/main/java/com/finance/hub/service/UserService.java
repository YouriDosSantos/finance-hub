package com.finance.hub.service;

import com.finance.hub.dataTransfer.RegisterUserDto;
import com.finance.hub.dataTransfer.UserDto;
import com.finance.hub.jdbcRepo.RoleJdbcRepository;
import com.finance.hub.jdbcRepo.UserJdbcRepository;
import com.finance.hub.model.Role;
import com.finance.hub.model.User;
import com.finance.hub.projection.UserDetailsProjection;
import com.finance.hub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    //not using this anymore for exchanging it with JDBC
//    @Autowired
//    private UserRepository repository;

    @Autowired
    private UserJdbcRepository userJdbcRepository;

    @Autowired
    private RoleJdbcRepository roleJdbcRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = userJdbcRepository.searchUserAndRolesByEmail(username);
        if(result.size() == 0) {
            throw new UsernameNotFoundException("User not Found");
        }
        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for(UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }

    protected User authenticated(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

            return userJdbcRepository.findUserWithRolesByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getMe(){
        User user = authenticated();
        return new UserDto(user);
    }

    @Transactional
    public void registerUser(RegisterUserDto registerUserDto) {
        //1- validates email
        Optional<User> existingEmail = userJdbcRepository.findByEmail(registerUserDto.getEmail());
        if(existingEmail.isPresent()){
            throw new IllegalArgumentException("Email already registered");
        }

        //2- create user
        User newUser = new User();
        newUser.setName(registerUserDto.getName());
        newUser.setEmail(registerUserDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        //3- Assign default role (ROLE_NEWUSER)
        Role defaultRole = roleJdbcRepository.findByAuthority("ROLE_NEWUSER");
        newUser.addRole(defaultRole);

        //4- Save
        userJdbcRepository.save(newUser);
    }
}
