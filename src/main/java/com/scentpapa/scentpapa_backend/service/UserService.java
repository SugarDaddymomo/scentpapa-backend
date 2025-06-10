package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> getCurrentUserDetails(User user);
}