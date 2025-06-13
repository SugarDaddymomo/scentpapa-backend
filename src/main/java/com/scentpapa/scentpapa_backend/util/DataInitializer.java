package com.scentpapa.scentpapa_backend.util;

import com.scentpapa.scentpapa_backend.models.Permission;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static com.scentpapa.scentpapa_backend.models.Role.ADMIN;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(adminEmail)) {
            User user = new User();
            user.setEmail(adminEmail);
            user.setRole(ADMIN);
            user.setPassword(passwordEncoder.encode(adminPassword)); // Ideally from config/env
            user.setFirstName("Admin");
            user.setPhoneNumber("+918527077014");

            Set<Permission> permissions = EnumSet.of(
                    Permission.ADMIN,
                    Permission.CATEGORY,
                    Permission.PRODUCTS
            );
            user.setPermissions(permissions);

            userRepository.save(user);
            System.out.println("Default admin created.");
        } else {
            System.out.println("Admin already exists, skipping creation.");
        }
    }
}
