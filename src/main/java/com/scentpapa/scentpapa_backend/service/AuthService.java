package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.requests.LoginRequest;
import com.scentpapa.scentpapa_backend.requests.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> signup(SignupRequest request);    //will change return type to have a respone class later
    ResponseEntity<?> login(LoginRequest request);
}