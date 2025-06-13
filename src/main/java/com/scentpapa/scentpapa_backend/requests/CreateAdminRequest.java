package com.scentpapa.scentpapa_backend.requests;

import com.scentpapa.scentpapa_backend.models.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdminRequest {
    private String email;
    private String password;
    private String firstName;
    private String phoneNumber;
    private Set<Permission> permissions;
}