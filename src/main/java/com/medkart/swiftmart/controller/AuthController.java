package com.medkart.swiftmart.controller;

import com.medkart.swiftmart.dto.LoginRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.dto.UserDto;
import com.medkart.swiftmart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody UserDto registrationReq) {
        return ResponseEntity.ok(userService.registerUser(registrationReq));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest req) throws InvalidCredentialsException {
        return ResponseEntity.ok(userService.loginUser(req));
    }

    @PreAuthorize("hasAuthority('ROOT_ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<Response> registerAdmin(@RequestBody UserDto registrationReq) {
        return ResponseEntity.ok(userService.registerAdmin(registrationReq));
    }
}
