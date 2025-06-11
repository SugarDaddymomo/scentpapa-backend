package com.scentpapa.scentpapa_backend.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateShippingAddressRequest {
    private Long newAddressId;
}