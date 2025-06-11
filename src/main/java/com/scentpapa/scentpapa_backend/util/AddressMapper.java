package com.scentpapa.scentpapa_backend.util;

import com.scentpapa.scentpapa_backend.dto.AddressDTO;
import com.scentpapa.scentpapa_backend.models.Address;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressDTO addressDTO, User user) {
        return Address.builder()
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .country(addressDTO.getCountry())
                .phoneNumber(addressDTO.getPhoneNumber())
                .streetAddress(addressDTO.getStreetAddress())
                .postalCode(addressDTO.getPostalCode())
                .residenceName(addressDTO.getResidenceName())
                .user(user)
                .isDefaultShipping(addressDTO.isDefaultShipping())
                .isDefaultBilling(addressDTO.isDefaultBilling())
                .build();
    }

    public AddressDTO toDto(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .city(address.getCity())
                .isDefaultShipping(address.isDefaultShipping())
                .isDefaultBilling(address.isDefaultBilling())
                .state(address.getState())
                .streetAddress(address.getStreetAddress())
                .country(address.getCountry())
                .phoneNumber(address.getPhoneNumber())
                .residenceName(address.getResidenceName())
                .postalCode(address.getPostalCode())
                .build();
    }
}