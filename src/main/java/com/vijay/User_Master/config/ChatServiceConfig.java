package com.vijay.User_Master.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class ChatServiceConfig {
    
    @Value("${chat.service.base-url:http://localhost:8080}")
    private String chatServiceBaseUrl;
    
    @Value("${chat.service.timeout:30}")
    private int timeoutSeconds;
    
    @Bean
    public WebClient chatServiceWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .followRedirect(true);
        
        return WebClient.builder()
                .baseUrl(chatServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }
}
