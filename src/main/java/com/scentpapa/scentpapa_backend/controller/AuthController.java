package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.requests.LoginRequest;
import com.scentpapa.scentpapa_backend.requests.SignupRequest;
import com.scentpapa.scentpapa_backend.service.AuthService;
import com.scentpapa.scentpapa_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/scentpapa/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (Objects.isNull(signupRequest) || !StringUtils.hasText(signupRequest.getEmail())
            || !StringUtils.hasText(signupRequest.getPassword()) || !StringUtils.hasText(signupRequest.getFirstName())
                || !StringUtils.hasText(signupRequest.getPhoneNumber())
        ) {
            return ResponseEntity.badRequest().body("Bad Request, Please provide proper request!");
        }
        return authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (Objects.isNull(loginRequest) ||
                !StringUtils.hasText(loginRequest.getEmail()) ||
                !StringUtils.hasText(loginRequest.getPassword())) {
            return ResponseEntity.badRequest().body("Bad Request: Email and password required.");
        }
        log.info("Attempting login using email: {}", loginRequest.getEmail());
        return authService.login(loginRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        // build and return user info DTO as needed
        return userService.getCurrentUserDetails(userDetails);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        //ONLY ADD SERVER SIDE LOGIC IF NEEDED AS OF NOW IT IS NOT OUR TOP CONCERN
        return ResponseEntity.ok("User logged out successfully!");
    }
}