package com.vijay.User_Master.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    private String message;
    private String provider;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private String conversationId;
    
    @JsonProperty("userId")
    @JsonAlias({"user_id", "user-id", "user"})
    private String userId;
    
    // API Keys for different providers
    private String openaiApiKey;
    private String claudeApiKey;
    private String groqApiKey;
    private String geminiApiKey;
    private String openrouterApiKey;
    private String huggingfaceApiKey;
    
    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getOpenaiApiKey() { return openaiApiKey; }
    public void setOpenaiApiKey(String openaiApiKey) { this.openaiApiKey = openaiApiKey; }
    
    public String getClaudeApiKey() { return claudeApiKey; }
    public void setClaudeApiKey(String claudeApiKey) { this.claudeApiKey = claudeApiKey; }
    
    public String getGroqApiKey() { return groqApiKey; }
    public void setGroqApiKey(String groqApiKey) { this.groqApiKey = groqApiKey; }
    
    public String getGeminiApiKey() { return geminiApiKey; }
    public void setGeminiApiKey(String geminiApiKey) { this.geminiApiKey = geminiApiKey; }
    
    public String getOpenrouterApiKey() { return openrouterApiKey; }
    public void setOpenrouterApiKey(String openrouterApiKey) { this.openrouterApiKey = openrouterApiKey; }
    
    public String getHuggingfaceApiKey() { return huggingfaceApiKey; }
    public void setHuggingfaceApiKey(String huggingfaceApiKey) { this.huggingfaceApiKey = huggingfaceApiKey; }
}