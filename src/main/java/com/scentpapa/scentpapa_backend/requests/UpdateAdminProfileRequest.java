package com.scentpapa.scentpapa_backend.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAdminProfileRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
}