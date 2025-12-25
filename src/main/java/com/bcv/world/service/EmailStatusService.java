package com.bcv.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcv.world.model.EmailJobStatus;
import com.bcv.world.model.JobDto;
import com.bcv.world.repository.EmailStatusRepository;

@Service
public class EmailStatusService {

    @Autowired
    private EmailStatusRepository repository;

    public void saveStatus(String email, JobDto job, String status) {
        EmailJobStatus entity = new EmailJobStatus(
                email,
                job.getCompany(),
                job.getLink(),
                job.getDate(),
                status
        );
        repository.save(entity);
    }
}

