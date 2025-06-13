package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.AdminUserDTO;
import com.scentpapa.scentpapa_backend.requests.CreateAdminRequest;
import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {
    String addAdmin(CreateAdminRequest request);
    List<AdminUserDTO> getAllAdmins();
    String toggleAdminLock(Long userId);
    String toggleAdminEnabled(Long userId);
    ResponseEntity<?> updateProfile(UpdateAdminProfileRequest request);
}