package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.AdminUserDTO;
import com.scentpapa.scentpapa_backend.models.Role;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.requests.CreateAdminRequest;
import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import com.scentpapa.scentpapa_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingServiceImpl ratingService;

    @Override
    public String addAdmin(CreateAdminRequest request) {
        Optional<User> userExists = userRepository.findByEmail(request.getEmail());
        if (userExists.isPresent()) {
            return "User already exists!";
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ADMIN)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .enabled(true)
                .accountNonLocked(true)
                .permissions(request.getPermissions())
                .build();
        userRepository.save(user);
        return "Added an ADMIN User";
    }

    @Override
    public List<AdminUserDTO> getAllAdmins() {
        return userRepository.findByRole(Role.ADMIN).stream()
                .map(user -> AdminUserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .phoneNumber(user.getPhoneNumber())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .accountNonLocked(user.isAccountNonLocked())
                        .enabled(user.isEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String toggleAdminLock(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can be locked/unlocked");
        }

        boolean currentStatus = user.isAccountNonLocked();
        user.setAccountNonLocked(!currentStatus);
        userRepository.save(user);

        return currentStatus ? "Admin locked successfully" : "Admin unlocked successfully";
    }

    @Override
    public String toggleAdminEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can be enabled/disabled");
        }

        boolean currentStatus = user.isEnabled();
        user.setEnabled(!currentStatus);
        userRepository.save(user);

        return currentStatus ? "Admin disabled successfully" : "Admin enabled successfully";
    }

    @Override
    public ResponseEntity<?> updateProfile(UpdateAdminProfileRequest request) {
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