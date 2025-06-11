package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.AddressDTO;
import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    List<AddressDTO> getAddressesForCurrentUser();
    AddressDTO getAddressForCurrentUser(Long addressId);
    AddressDTO markAddressAsDefaultShipping(Long addressId);
    AddressDTO markAddressAsDefaultBilling(Long addressId);
    void deleteAddress(Long addressId);
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);
}