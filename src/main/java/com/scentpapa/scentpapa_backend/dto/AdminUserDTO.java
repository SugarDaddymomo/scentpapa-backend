package com.scentpapa.scentpapa_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String phoneNumber;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean accountNonLocked;
    private boolean enabled;
}
