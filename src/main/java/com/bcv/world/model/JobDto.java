package com.bcv.world.model;

import java.time.LocalDate;


public class JobDto {
    private String company;
    private String link;
    private LocalDate date;
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public JobDto(String company, String link, LocalDate date) {
		super();
		this.company = company;
		this.link = link;
		this.date = date;
	}
	
}
