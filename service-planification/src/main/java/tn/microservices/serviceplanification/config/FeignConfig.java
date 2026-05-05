package tn.microservices.serviceplanification.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            // 🔥 Hardcoded token (for now)
            String token = "Bearer YOUR_VALID_JWT_HERE";

            requestTemplate.header("Authorization", token);
        };
    }
}
