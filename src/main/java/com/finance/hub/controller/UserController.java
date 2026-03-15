package com.finance.hub.controller;

import com.finance.hub.dataTransfer.ContactDto;
import com.finance.hub.dataTransfer.RegisterUserDto;
import com.finance.hub.dataTransfer.UserDto;
import com.finance.hub.record.ChangePasswordRequest;
import com.finance.hub.service.ContactService;
import com.finance.hub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //    Get by ID

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_NEWUSER')")
    @GetMapping("/me")
    public ResponseEntity<UserDto> findMe(){
        UserDto userDto = userService.getMe();

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register-user")
    public void registerUser(@RequestBody RegisterUserDto registerUserDto){
        userService.registerUser(registerUserDto);
    }

    @PostMapping
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.currentPassword(), request.newPassword());
    }
}
