package com.scentpapa.scentpapa_backend.controller;


import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import com.scentpapa.scentpapa_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scentpapa/v1/admin/profile")
@RequiredArgsConstructor
@Slf4j
public class AdminProfileController {

    private final AdminService adminService;

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminDetails(@RequestBody UpdateAdminProfileRequest request) {
        return adminService.updateProfile(request);
    }
}