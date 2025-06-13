package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    ResponseEntity<?> updateCustomerProfile(UpdateAdminProfileRequest request);
}