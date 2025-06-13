package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Role;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'")
    Long countCustomers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    Long countAdmins();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u.createdAt BETWEEN :start AND :end")
    Long countNewCustomersBetween(Instant start, Instant end);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND EXISTS (SELECT o FROM Order o WHERE o.user = u)")
    Long countReturningCustomers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND NOT EXISTS (SELECT o FROM Order o WHERE o.user = u)")
    Long countNewCustomersWithoutOrders();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.id = :userId")
    Optional<User> findUserWithAddressesById(@Param("userId") Long userId);
}
