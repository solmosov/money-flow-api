package com.example.moneyflowapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN FETCH u.roles
            WHERE LOWER(u.email) = LOWER(:email)
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}
