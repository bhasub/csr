package com.emc.ideaforce.repository;

import com.emc.ideaforce.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetRequest, Long> {

    PasswordResetRequest findByToken(String token);

}
