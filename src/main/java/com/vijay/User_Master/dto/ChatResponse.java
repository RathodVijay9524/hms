package com.vijay.User_Master.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String response;
    private String conversationId;
    private String provider;
    private String model;
    private Long tokensUsed;
    private Long responseTime;
    private String error;
    
    // Getters and setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Long getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Long tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}