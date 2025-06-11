package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Address;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}