package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.ChatRequest;
import com.vijay.User_Master.dto.ChatResponse;
import com.vijay.User_Master.dto.ProviderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class ChatIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatIntegrationService.class);
    
    private final WebClient webClient;
    
    @Value("${chat.service.base-url:http://localhost:8080}")
    private String chatServiceBaseUrl;
    
    @Value("${chat.service.timeout:30}")
    private int timeoutSeconds;
    
    public ChatIntegrationService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    /**
     * Send a message to the chat service
     */
    public ChatResponse sendMessage(ChatRequest request) {
        logger.info("Forwarding chat message to chat service: {}", request.getMessage());
        
        try {
            ChatResponse response = webClient.post()
                    .uri(chatServiceBaseUrl + "/api/chat/message")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
                    
            logger.info("Successfully received response from chat service");
            return response;
            
        } catch (Exception e) {
            logger.error("Error communicating with chat service: {}", e.getMessage());
            return ChatResponse.builder()
                    .error("Failed to communicate with chat service: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Get all available providers from the chat service
     */
    public List<ProviderInfo> getProviders() {
        logger.info("Fetching providers from chat service");
        
        try {
            List<ProviderInfo> providers = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/chat/providers")
                    .retrieve()
                    .bodyToFlux(ProviderInfo.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
                    
            logger.info("Successfully fetched {} providers from chat service", providers.size());
            return providers;
            
        } catch (Exception e) {
            logger.error("Error fetching providers from chat service: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get models for a specific provider from the chat service
     */
    public List<String> getModelsForProvider(String providerName) {
        logger.info("Fetching models for provider: {} from chat service", providerName);
        
        try {
            List<String> models = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/chat/providers/" + providerName + "/models")
                    .retrieve()
                    .bodyToFlux(String.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched {} models for provider {}", models.size(), providerName);
            return models;
            
        } catch (Exception e) {
            logger.error("Error fetching models for provider {} from chat service: {}", providerName, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get user's chat list from the chat service
     */
    public List<Object> getUserChatList(String userId) {
        logger.info("Fetching chat list for user: {} from chat service", userId);
        
        try {
            // Parse the response as a List of Objects (conversations)
            List<Object> chats = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/chat/users/" + userId + "/chats")
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorReturn(List.of()) // Return empty list on error
                    .block();
            
            if (chats == null) {
                chats = List.of();
            }
            
            logger.info("Successfully fetched {} chats for user {}", chats.size(), userId);
            return chats;
            
        } catch (Exception e) {
            logger.error("Error fetching chat list for user {} from chat service: {}", userId, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get conversation messages from the chat service
     */
    public List<Object> getConversationMessages(String userId, String conversationId) {
        logger.info("Fetching messages for conversation: {} of user: {} from chat service", conversationId, userId);
        
        try {
            // Parse the response as a List of Objects (messages)
            List<Object> messages = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/chat/users/" + userId + "/conversations/" + conversationId + "/messages")
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorReturn(List.of()) // Return empty list on error
                    .block();
                    
            if (messages == null) {
                messages = List.of();
            }
            
            logger.info("Successfully fetched {} messages for conversation {}", messages.size(), conversationId);
            return messages;
            
        } catch (Exception e) {
            logger.error("Error fetching messages for conversation {} from chat service: {}", conversationId, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get user's chat statistics from the chat service
     */
    public Object getUserChatStats(String userId) {
        logger.info("Fetching chat stats for user: {} from chat service", userId);
        
        try {
            Object stats = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/chat/users/" + userId + "/stats")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched chat stats for user {}", userId);
            return stats;
            
        } catch (Exception e) {
            logger.error("Error fetching chat stats for user {} from chat service: {}", userId, e.getMessage());
            return null;
        }
    }
    
    // Async versions for better performance
    public Mono<ChatResponse> sendMessageAsync(ChatRequest request) {
        return webClient.post()
                .uri(chatServiceBaseUrl + "/api/chat/message")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .timeout(Duration.ofSeconds(timeoutSeconds));
    }
    
    public Mono<List<ProviderInfo>> getProvidersAsync() {
        return webClient.get()
                .uri(chatServiceBaseUrl + "/api/chat/providers")
                .retrieve()
                .bodyToFlux(ProviderInfo.class)
                .collectList()
                .timeout(Duration.ofSeconds(timeoutSeconds));
    }
    
    public Mono<List<String>> getModelsForProviderAsync(String providerName) {
        return webClient.get()
                .uri(chatServiceBaseUrl + "/api/chat/providers/" + providerName + "/models")
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .timeout(Duration.ofSeconds(timeoutSeconds));
    }
    
    // MCP Server Integration Methods
    
    /**
     * Get all available MCP tools from chat service
     */
    public Map<String, Object> getMcpTools() {
        logger.info("Fetching MCP tools from chat service");
        
        try {
            Map<String, Object> response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/tools")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched MCP tools from chat service");
            return response != null ? response : Map.of("tools", List.of(), "count", 0);
            
        } catch (Exception e) {
            logger.error("Error fetching MCP tools from chat service: {}", e.getMessage());
            return Map.of("error", e.getMessage(), "tools", List.of(), "count", 0);
        }
    }
    
    /**
     * Get MCP injection status from chat service
     */
    public Map<String, Object> getMcpInjectionStatus() {
        logger.info("Fetching MCP injection status from chat service");
        
        try {
            Map<String, Object> response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/injection-status")
                .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched MCP injection status from chat service");
            return response != null ? response : Map.of("injectionStatus", "unknown");
            
        } catch (Exception e) {
            logger.error("Error fetching MCP injection status from chat service: {}", e.getMessage());
            return Map.of("error", e.getMessage(), "injectionStatus", "error");
        }
    }
    
    /**
     * Start a specific MCP server
     */
    public Map<String, Object> startMcpServer(String serverId) {
        logger.info("Starting MCP server: {} via chat service", serverId);
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId + "/start")
                .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully started MCP server: {}", serverId);
            return response != null ? response : Map.of("success", false, "message", "Unknown response");
            
        } catch (Exception e) {
            logger.error("Error starting MCP server {} via chat service: {}", serverId, e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    /**
     * Stop a specific MCP server
     */
    public Map<String, Object> stopMcpServer(String serverId) {
        logger.info("Stopping MCP server: {} via chat service", serverId);
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId + "/stop")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully stopped MCP server: {}", serverId);
            return response != null ? response : Map.of("success", false, "message", "Unknown response");
            
        } catch (Exception e) {
            logger.error("Error stopping MCP server {} via chat service: {}", serverId, e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    /**
     * Get tools for a specific MCP server
     */
    public Map<String, Object> getMcpServerTools(String serverId) {
        logger.info("Fetching tools for MCP server: {} from chat service", serverId);
        
        try {
            Map<String, Object> response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId + "/tools")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched tools for MCP server: {}", serverId);
            return response != null ? response : Map.of("tools", List.of(), "count", 0);
            
        } catch (Exception e) {
            logger.error("Error fetching tools for MCP server {} from chat service: {}", serverId, e.getMessage());
            return Map.of("error", e.getMessage(), "tools", List.of(), "count", 0);
        }
    }
    
    /**
     * Get all MCP servers (simple list)
     */
    public Object getAllMcpServersSimple() {
        logger.info("Fetching all MCP servers from chat service");
        
        try {
            Object response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully fetched all MCP servers from chat service");
            return response != null ? response : List.of();
            
        } catch (Exception e) {
            logger.error("Error fetching all MCP servers from chat service: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Add a new MCP server
     */
    public Map<String, Object> addMcpServer(Map<String, Object> serverConfig) {
        logger.info("Adding MCP server via chat service");
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers")
                    .bodyValue(serverConfig)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully added MCP server via chat service");
            return response != null ? response : Map.of("success", false, "message", "Unknown response");
            
        } catch (Exception e) {
            logger.error("Error adding MCP server via chat service: {}", e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    /**
     * Remove a MCP server
     */
    public Map<String, Object> removeMcpServer(String serverId) {
        logger.info("Removing MCP server: {} via chat service", serverId);
        
        try {
            Map<String, Object> response = webClient.delete()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully removed MCP server: {}", serverId);
            return response != null ? response : Map.of("success", false, "message", "Unknown response");
            
        } catch (Exception e) {
            logger.error("Error removing MCP server {} via chat service: {}", serverId, e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    /**
     * Refresh tool cache for a specific MCP server
     */
    public Map<String, Object> refreshMcpToolCache(String serverId) {
        logger.info("Refreshing tool cache for MCP server: {} via chat service", serverId);
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId + "/refresh-cache")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            logger.info("Successfully refreshed tool cache for MCP server: {}", serverId);
            return response != null ? response : Map.of("success", false, "message", "Unknown response");
            
        } catch (Exception e) {
            logger.error("Error refreshing tool cache for MCP server {} via chat service: {}", serverId, e.getMessage());
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    public Map<String, Object> getAllMcpServers() {
        logger.info("Fetching all MCP servers via chat service");
        try {
            Map<String, Object> response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/servers")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            logger.info("Successfully fetched all MCP servers");
            return response != null ? response : Map.of("servers", List.of(), "totalCount", 0);
        } catch (Exception e) {
            logger.error("Error fetching all MCP servers via chat service: {}", e.getMessage());
            return Map.of("servers", List.of(), "totalCount", 0, "error", e.getMessage());
        }
    }

    public Map<String, Object> getMcpServerStatus(String serverId) {
        logger.info("Fetching status for MCP server: {} via chat service", serverId);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(chatServiceBaseUrl + "/api/mcp-servers/" + serverId + "/status")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            logger.info("Successfully fetched status for MCP server: {}", serverId);
            return response != null ? response : Map.of("serverId", serverId, "status", "UNKNOWN");
        } catch (Exception e) {
            logger.error("Error fetching status for MCP server {} via chat service: {}", serverId, e.getMessage());
            return Map.of("serverId", serverId, "status", "ERROR", "error", e.getMessage());
        }
    }
}