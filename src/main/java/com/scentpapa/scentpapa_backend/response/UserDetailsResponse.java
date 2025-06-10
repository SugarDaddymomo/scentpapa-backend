package com.scentpapa.scentpapa_backend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private String role;
}