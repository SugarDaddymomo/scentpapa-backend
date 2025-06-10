package com.scentpapa.scentpapa_backend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String firstName;
    private String email;
    private String role;
}