package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.AddressDTO;
import com.scentpapa.scentpapa_backend.models.Address;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.AddressRepository;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.service.AddressService;
import com.scentpapa.scentpapa_backend.util.AddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final RatingServiceImpl ratingService;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Address address = addressMapper.toEntity(addressDTO, user);
        if (address.isDefaultShipping()) {
            unsetDefaultShipping(user);
        }
        if (address.isDefaultBilling()) {
            unsetDefaultBilling(user);
        }
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public List<AddressDTO> getAddressesForCurrentUser() {
        User user = ratingService.getCurrentAuthenticatedUser();
        List<Address> addressList = addressRepository.findByUser(user);
        return addressList.stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddressForCurrentUser(Long addressId) {
        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new RuntimeException("Address not found!"));
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found!"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }

        address.setResidenceName(addressDTO.getResidenceName());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());

        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found!"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }
        List<Address> userAddresses = addressRepository.findByUser(user);
        if (userAddresses.size() <= 1) {
            throw new RuntimeException("Cannot delete the only address. At least one address must remain.");
        }

        boolean wasDefaultShipping = address.isDefaultShipping();
        boolean wasDefaultBilling = address.isDefaultBilling();

        addressRepository.delete(address);
        if (wasDefaultShipping || wasDefaultBilling) {
            List<Address> remainingAddresses = addressRepository.findByUser(user);

            Address newDefaultAddress = remainingAddresses.getFirst();
            if (wasDefaultShipping) {
                newDefaultAddress.setDefaultShipping(true);
            }
            if (wasDefaultBilling) {
                newDefaultAddress.setDefaultBilling(true);
            }

            addressRepository.save(newDefaultAddress);
        }
    }

    @Override
    public AddressDTO markAddressAsDefaultShipping(Long addressId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new RuntimeException("Address not found!"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }
        unsetDefaultShipping(user);
        address.setDefaultShipping(true);
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDTO markAddressAsDefaultBilling(Long addressId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new RuntimeException("Address not found!"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access!");
        }
        unsetDefaultBilling(user);
        address.setDefaultBilling(true);
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    private void unsetDefaultShipping(User user) {
        User userWithAddresses = userRepository.findUserWithAddressesById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        userWithAddresses.getAddresses().stream()
                .filter(Address::isDefaultShipping)
                .forEach(addr -> {
                    addr.setDefaultShipping(false);
                    addressRepository.save(addr);
                });
    }

    private void unsetDefaultBilling(User user) {
        User userWithAddresses = userRepository.findUserWithAddressesById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        userWithAddresses.getAddresses().stream()
                .filter(Address::isDefaultBilling)
                .forEach(addr -> {
                    addr.setDefaultBilling(false);
                    addressRepository.save(addr);
                });
    }
}