package com.sample.apiProcessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiData {
	@JsonProperty
    private int id;
   
    @JsonProperty
    private String description;

    public ApiData() {
		//Auto-generated constructor stub
	}
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public String toString() {
		return new StringBuffer("{\"apiData\":{")
        		.append("\"id\":\"")
        		.append(this.id)
        		.append("\",\"description\":\"")
        		.append(this.description)
        		.append("\"}}")
        		.toString();	
    }
}
