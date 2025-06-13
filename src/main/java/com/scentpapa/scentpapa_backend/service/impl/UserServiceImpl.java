package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.models.Role;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.requests.LoginRequest;
import com.scentpapa.scentpapa_backend.requests.SignupRequest;
import com.scentpapa.scentpapa_backend.response.LoginResponse;
import com.scentpapa.scentpapa_backend.response.UserDetailsResponse;
import com.scentpapa.scentpapa_backend.service.UserService;
import com.scentpapa.scentpapa_backend.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static com.scentpapa.scentpapa_backend.models.Role.CUSTOMER;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public ResponseEntity<?> getCurrentUserDetails(User user) {
        Set<String> permissions = null;
        if (user.getRole() == Role.ADMIN) {
            permissions = user.getPermissions()
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());
        }
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(StringUtils.hasText(user.getLastName()) ? user.getLastName() : " ")
                .phoneNumber(user.getPhoneNumber())
                .username(StringUtils.hasText(user.getUsername()) ? user.getUsername() : " ")
                .role(user.getRole().name())
                .permissions(permissions)
                .build();
        return ResponseEntity.ok(userDetailsResponse);
    }
}
