package com.bcv.world.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_email_status")
public class UserEmailStatus {

    @Id 
    private String email;

    private String name;
    private boolean isEmailSent;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEmailSent() {
		return isEmailSent;
	}
	public void setEmailSent(boolean isEmailSent) {
		this.isEmailSent = isEmailSent;
	}

   
}

