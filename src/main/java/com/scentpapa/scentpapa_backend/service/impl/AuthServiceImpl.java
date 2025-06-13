package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.requests.LoginRequest;
import com.scentpapa.scentpapa_backend.requests.SignupRequest;
import com.scentpapa.scentpapa_backend.response.LoginResponse;
import com.scentpapa.scentpapa_backend.service.AuthService;
import com.scentpapa.scentpapa_backend.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.scentpapa.scentpapa_backend.models.Role.CUSTOMER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<?> signup(SignupRequest request) {
        log.info("Shuru signup 1");
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(StringUtils.hasText(request.getLastName()) ? request.getLastName() : " ")
                .phoneNumber(request.getPhoneNumber())
                .role(CUSTOMER)
                .password(passwordEncoder.encode(request.getPassword()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .credentialsNonExpired(true)
                .build();

        userRepository.save(user);
        log.info("signup db success");

        //send welcome mail
        //sendWelcomeMail(String email, String firstName);
        log.info("welcome mail done exiting now");
        return ResponseEntity.ok("User registered successfully.");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid email or User not found.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(LoginResponse.builder().token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .role(user.getRole().name())
                .build()
        );
    }
}