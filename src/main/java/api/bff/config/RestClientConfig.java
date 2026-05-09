package api.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${microservices.auth.url}")
    private String authUrl;

    @Value("${microservices.inventory.url}")
    private String inventoryUrl;

    @Bean
    public RestClient authRestClient() {
        return RestClient.builder()
                .baseUrl(authUrl)
                .build();
    }

    @Bean
    public RestClient inventoryRestClient() {
        return RestClient.builder()
                .baseUrl(inventoryUrl)
                .build();
    }
}
