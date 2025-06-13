package com.scentpapa.scentpapa_backend.controller;


import com.scentpapa.scentpapa_backend.requests.UpdateAdminProfileRequest;
import com.scentpapa.scentpapa_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scentpapa/v1/customer/profile")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateAdminDetails(@RequestBody UpdateAdminProfileRequest request) {
        return customerService.updateCustomerProfile(request);
    }
}