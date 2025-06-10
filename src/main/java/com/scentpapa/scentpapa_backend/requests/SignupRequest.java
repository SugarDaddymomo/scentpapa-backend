package com.scentpapa.scentpapa_backend.requests;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SignupRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}