package com.example.domain;

public class Phone {
	private String personName;
	private String number;
	public Phone(String personName, String number) {
		this.personName = personName;
		this.number = number;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
}
