package com.bcv.world.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcv.world.model.EmailJobStatus;

@Repository
public interface EmailStatusRepository 
        extends JpaRepository<EmailJobStatus, Long> {

    boolean existsByEmailAndJobLinkAndJobDate(
            String email,
            String jobLink,
            LocalDate jobDate
    );
}
