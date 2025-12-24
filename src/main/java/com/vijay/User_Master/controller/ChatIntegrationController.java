package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.ChatRequest;
import com.vijay.User_Master.dto.ChatResponse;
import com.vijay.User_Master.dto.ProviderInfo;
import com.vijay.User_Master.service.ChatIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatIntegrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatIntegrationController.class);
    
    private final ChatIntegrationService chatIntegrationService;
    
    @Autowired
    public ChatIntegrationController(ChatIntegrationService chatIntegrationService) {
        this.chatIntegrationService = chatIntegrationService;
    }
    
    /**
     * Send a message to the chat service
     * Exposes the same endpoint as your original chat service
     */
    @PostMapping(value = "/message", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        logger.info("Received chat message request: {}", request);
        
        try {
            // Auto-populate userId from JWT token if not provided
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                // TODO: Extract userId from JWT token
                logger.warn("No userId provided in request, using default");
            }
            
            ChatResponse response = chatIntegrationService.sendMessage(request);
            logger.info("Successfully processed chat message request");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing chat message request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get all available providers
     * Exposes the same endpoint as your original chat service
     */
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderInfo>> getProviders() {
        logger.info("Received request to fetch providers");
        
        try {
            List<ProviderInfo> providers = chatIntegrationService.getProviders();
            logger.info("Successfully fetched {} providers", providers.size());
            return ResponseEntity.ok(providers);
            
        } catch (Exception e) {
            logger.error("Error fetching providers: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get models for a specific provider
     * Exposes the same endpoint as your original chat service
     */
    @GetMapping("/providers/{providerName}/models")
    public ResponseEntity<List<String>> getModelsForProvider(@PathVariable String providerName) {
        logger.info("Received request to fetch models for provider: {}", providerName);
        
        try {
            List<String> models = chatIntegrationService.getModelsForProvider(providerName);
            logger.info("Successfully fetched {} models for provider {}", models.size(), providerName);
            return ResponseEntity.ok(models);
            
        } catch (Exception e) {
            logger.error("Error fetching models for provider {}: {}", providerName, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get user's chat list/conversations
     */
    @GetMapping("/users/{userId}/chats")
    public ResponseEntity<List<Object>> getUserChatList(@PathVariable String userId) {
        logger.info("Received request to fetch chat list for user: {}", userId);
        
        try {
            List<Object> chats = chatIntegrationService.getUserChatList(userId);
            logger.info("Successfully fetched {} chats for user {}", chats.size(), userId);
            return ResponseEntity.ok(chats);
            
        } catch (Exception e) {
            logger.error("Error fetching chat list for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get messages for a specific conversation
     */
    @GetMapping("/users/{userId}/conversations/{conversationId}/messages")
    public ResponseEntity<List<Object>> getConversationMessages(
            @PathVariable String userId, 
            @PathVariable String conversationId) {
        logger.info("Received request to fetch messages for conversation: {} of user: {}", conversationId, userId);
        
        try {
            List<Object> messages = chatIntegrationService.getConversationMessages(userId, conversationId);
            logger.info("Successfully fetched {} messages for conversation {}", messages.size(), conversationId);
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            logger.error("Error fetching messages for conversation {}: {}", conversationId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get user's chat statistics
     */
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<Object> getUserChatStats(@PathVariable String userId) {
        logger.info("Received request to fetch chat stats for user: {}", userId);
        
        try {
            Object stats = chatIntegrationService.getUserChatStats(userId);
            logger.info("Successfully fetched chat stats for user {}", userId);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error fetching chat stats for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Async endpoints for better performance (optional)
    /**
     * Async version of sendMessage
     */
    @PostMapping(value = "/message/async", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<ChatResponse>> sendMessageAsync(@RequestBody ChatRequest request) {
        logger.info("Received async chat message request: {}", request);
        
        return chatIntegrationService.sendMessageAsync(request)
                .map(response -> {
                    logger.info("Successfully processed async chat message request");
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Async version of getProviders
     */
    @GetMapping("/providers/async")
    public Mono<ResponseEntity<List<ProviderInfo>>> getProvidersAsync() {
        logger.info("Received async request to fetch providers");
        
        return chatIntegrationService.getProvidersAsync()
                .map(providers -> {
                    logger.info("Successfully fetched {} providers asynchronously", providers.size());
                    return ResponseEntity.ok(providers);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Async version of getModelsForProvider
     */
    @GetMapping("/providers/{providerName}/models/async")
    public Mono<ResponseEntity<List<String>>> getModelsForProviderAsync(@PathVariable String providerName) {
        logger.info("Received async request to fetch models for provider: {}", providerName);
        
        return chatIntegrationService.getModelsForProviderAsync(providerName)
                .map(models -> {
                    logger.info("Successfully fetched {} models for provider {} asynchronously", models.size(), providerName);
                    return ResponseEntity.ok(models);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
}