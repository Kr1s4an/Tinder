package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.model.Verification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findByToken(String token);

    Optional<Verification> findTokenByUserId(Long userId);
}
