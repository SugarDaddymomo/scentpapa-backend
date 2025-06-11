package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.AddressDTO;
import com.scentpapa.scentpapa_backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/scentpapa/v1/address")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> addUserAddress(@RequestBody AddressDTO addressDTO) {
        AddressDTO address = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(address, CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        List<AddressDTO> addresses = addressService.getAddressesForCurrentUser();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> getUserAddress(@PathVariable Long addressId) {
        AddressDTO address = addressService.getAddressForCurrentUser(addressId);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> updateUserAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        AddressDTO address = addressService.updateAddress(addressId, addressDTO);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteUserAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default-shipping")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> setDefaultShipping(@PathVariable Long addressId) {
        log.info("default shipping mark kro shuru 1");
        AddressDTO address = addressService.markAddressAsDefaultShipping(addressId);
        return ResponseEntity.ok(address);
    }

    @PatchMapping("/{addressId}/default-billing")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> setDefaultBilling(@PathVariable Long addressId) {
        AddressDTO address = addressService.markAddressAsDefaultBilling(addressId);
        return ResponseEntity.ok(address);
    }
}