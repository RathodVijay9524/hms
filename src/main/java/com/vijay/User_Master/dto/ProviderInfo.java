package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProviderInfo {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("displayName")
    private String displayName;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("available")
    private boolean available;
    
    // Default constructor
    public ProviderInfo() {}
    
    // Constructor with parameters
    public ProviderInfo(String name, String displayName, String description, boolean available) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.available = available;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return "ProviderInfo{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                '}';
    }
}
