package com.hth.util.enums;

public enum CardType {
	NONE(""),
	GROUP("Group"),
	DIVISION("Division"),
	EMPLOYEE("Employee Family Card"),
	OUTSTANDING("Outstanding Cards"),
	INDIVIDUAL("Individual Member Card");
	
	private final String description;
	
	CardType(String description) {
		this.description = description;
	}
	
	public String description() {
		return description;
	}
}
