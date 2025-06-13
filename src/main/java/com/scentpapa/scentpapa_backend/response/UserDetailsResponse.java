package com.scentpapa.scentpapa_backend.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDetailsResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private String role;
    private Set<String> permissions;
}