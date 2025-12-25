package com.bcv.world.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EmailJobStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String companyName;
    private String jobLink;
    private LocalDate jobDate;
    private String status;

    // 🔹 REQUIRED by JPA
    public EmailJobStatus() {
    }

    // 🔹 REQUIRED by your service call
    public EmailJobStatus(
            String email,
            String companyName,
            String jobLink,
            LocalDate jobDate,
            String status
    ) {
        this.email = email;
        this.companyName = companyName;
        this.jobLink = jobLink;
        this.jobDate = jobDate;
        this.status = status;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getJobLink() {
		return jobLink;
	}

	public void setJobLink(String jobLink) {
		this.jobLink = jobLink;
	}

	public LocalDate getJobDate() {
		return jobDate;
	}

	public void setJobDate(LocalDate jobDate) {
		this.jobDate = jobDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    // getters & setters
    
}
