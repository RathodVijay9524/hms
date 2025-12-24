package com.vijay.User_Master.controller;

import com.vijay.User_Master.service.ChatIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mcp-servers")
@CrossOrigin(origins = "*")
public class McpIntegrationController {

    private static final Logger logger = LoggerFactory.getLogger(McpIntegrationController.class);

    @Autowired
    private ChatIntegrationService chatIntegrationService;

    /**
     * Get all available MCP tools from chat service
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getAvailableTools() {
        logger.info("Received request to fetch MCP tools");
        try {
            Map<String, Object> response = chatIntegrationService.getMcpTools();
            logger.info("Successfully fetched MCP tools");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching MCP tools: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching MCP tools: " + e.getMessage()));
        }
    }

    /**
     * Get MCP injection status from chat service
     */
    @GetMapping("/injection-status")
    public ResponseEntity<Map<String, Object>> getInjectionStatus() {
        logger.info("Received request to fetch MCP injection status");
        try {
            Map<String, Object> response = chatIntegrationService.getMcpInjectionStatus();
            logger.info("Successfully fetched MCP injection status");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching MCP injection status: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching injection status: " + e.getMessage()));
        }
    }

    /**
     * Start a specific MCP server
     */
    @PostMapping("/{serverId}/start")
    public ResponseEntity<Map<String, Object>> startServer(@PathVariable String serverId) {
        logger.info("Received request to start MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.startMcpServer(serverId);
            logger.info("Successfully processed start request for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error starting MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error starting MCP server: " + e.getMessage()));
        }
    }

    /**
     * Stop a specific MCP server
     */
    @PostMapping("/{serverId}/stop")
    public ResponseEntity<Map<String, Object>> stopServer(@PathVariable String serverId) {
        logger.info("Received request to stop MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.stopMcpServer(serverId);
            logger.info("Successfully processed stop request for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error stopping MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error stopping MCP server: " + e.getMessage()));
        }
    }

    /**
     * Get tools for a specific MCP server
     */
    @GetMapping("/{serverId}/tools")
    public ResponseEntity<Map<String, Object>> getServerTools(@PathVariable String serverId) {
        logger.info("Received request to fetch tools for MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.getMcpServerTools(serverId);
            logger.info("Successfully fetched tools for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching tools for MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching server tools: " + e.getMessage()));
        }
    }

    /**
     * Get all MCP servers (simple list)
     */
    @GetMapping
    public ResponseEntity<Object> getAllServersSimple() {
        logger.info("Received request to fetch all MCP servers");
        try {
            Object response = chatIntegrationService.getAllMcpServersSimple();
            logger.info("Successfully fetched all MCP servers");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching all MCP servers: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching MCP servers: " + e.getMessage()));
        }
    }

    /**
     * Add a new MCP server
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addServer(@RequestBody Map<String, Object> serverConfig) {
        logger.info("Received request to add MCP server");
        try {
            Map<String, Object> response = chatIntegrationService.addMcpServer(serverConfig);
            logger.info("Successfully processed add MCP server request");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding MCP server: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error adding MCP server: " + e.getMessage()));
        }
    }

    /**
     * Remove a MCP server
     */
    @DeleteMapping("/{serverId}")
    public ResponseEntity<Map<String, Object>> removeServer(@PathVariable String serverId) {
        logger.info("Received request to remove MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.removeMcpServer(serverId);
            logger.info("Successfully processed remove request for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error removing MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error removing MCP server: " + e.getMessage()));
        }
    }

    /**
     * Refresh tool cache for a specific MCP server
     */
    @PostMapping("/{serverId}/refresh-cache")
    public ResponseEntity<Map<String, Object>> refreshToolCache(@PathVariable String serverId) {
        logger.info("Received request to refresh tool cache for MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.refreshMcpToolCache(serverId);
            logger.info("Successfully refreshed tool cache for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error refreshing tool cache for MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error refreshing tool cache: " + e.getMessage()));
        }
    }

    @GetMapping("/servers")
    public ResponseEntity<Map<String, Object>> getAllServers() {
        logger.info("Received request to fetch all MCP servers");
        try {
            Map<String, Object> response = chatIntegrationService.getAllMcpServers();
            logger.info("Successfully fetched all MCP servers");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching all MCP servers: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching servers: " + e.getMessage()));
        }
    }

    @GetMapping("/{serverId}/status")
    public ResponseEntity<Map<String, Object>> getServerStatus(@PathVariable String serverId) {
        logger.info("Received request to get status for MCP server: {}", serverId);
        try {
            Map<String, Object> response = chatIntegrationService.getMcpServerStatus(serverId);
            logger.info("Successfully fetched status for MCP server: {}", serverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching status for MCP server {}: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error fetching server status: " + e.getMessage()));
        }
    }
}
