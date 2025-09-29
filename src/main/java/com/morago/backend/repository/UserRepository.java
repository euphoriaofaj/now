package com.morago.backend.repository;

import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.exception.UserNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String userName);

    boolean existsByUsername(String username);
    boolean existsByIdAndUsername(Long id, String username);

    @EntityGraph(attributePaths = {"roles", "userProfile", "translatorProfile"})
    Optional<User> findWithProfilesById(Long id);

    boolean existsByIdAndIsActiveTrue(Long id);

    boolean existsByIdAndRoles_Name(Long id, Roles role);

    boolean existsByIdAndTranslatorProfileIsNotNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(Long id);
}
