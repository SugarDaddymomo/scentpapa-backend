package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import com.scentpapa.scentpapa_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final RatingServiceImpl ratingService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> updateCustomerProfile(UpdateAdminProfileRequest request) {
        User currentUser = ratingService.getCurrentAuthenticatedUser();
        if (StringUtils.hasText(request.getUsername())) {
            Optional<User> userByUsername = userRepository.findByUsername(request.getUsername());
            if (userByUsername.isPresent() && !userByUsername.get().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
            }
            currentUser.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getFirstName())) {
            currentUser.setFirstName(request.getFirstName());
        }
        if (StringUtils.hasText(request.getLastName())) {
            currentUser.setLastName(request.getLastName());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            currentUser.setPhoneNumber(request.getPhoneNumber());
        }

        if (StringUtils.hasText(request.getNewPassword())) {
            // Check current password is valid
            if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid current password.");
            }
            currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        userRepository.save(currentUser);
        return ResponseEntity.ok("Profile updated successfully.");
    }
}