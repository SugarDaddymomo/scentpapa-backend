package com.scentpapa.scentpapa_backend.controller;


import com.scentpapa.scentpapa_backend.dto.AdminUserDTO;
import com.scentpapa.scentpapa_backend.requests.CreateAdminRequest;
import com.scentpapa.scentpapa_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scentpapa/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_ADMIN')")
    public ResponseEntity<?> addAdmin(@RequestBody CreateAdminRequest adminRequest) {
        String response = adminService.addAdmin(adminRequest);
        if (response.equals("User already exists!")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PatchMapping("/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_ADMIN')")
    public ResponseEntity<String> toggleLockAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleAdminLock(userId));
    }

    @PatchMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_ADMIN')")
    public ResponseEntity<String> toggleEnableAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleAdminEnabled(userId));
    }
}