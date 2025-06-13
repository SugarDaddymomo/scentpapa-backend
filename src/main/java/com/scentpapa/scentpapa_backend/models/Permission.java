package com.scentpapa.scentpapa_backend.models;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    ADMIN,
    PRODUCTS,
    CATEGORY;

    @Override
    public String getAuthority() {
        return "PERMISSION_" + this.name();
    }
}