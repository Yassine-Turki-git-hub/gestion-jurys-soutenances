package tn.microservices.apigateway.config;
import lombok.RequiredArgsConstructor;
import tn.microservices.apigateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Service Utilisateurs
                .route("service-utilisateurs", r -> r
                        .path("/api/utilisateurs/**", "/api/enseignants/**", "/api/etudiants/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("${UTILISATEURS_SERVICE_URL:http://localhost:8081}"))

                // Service Soutenance
                .route("service-soutenance", r -> r
                        .path("/api/soutenances/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("${SOUTENANCE_SERVICE_URL:http://localhost:8082}"))

                // Service Planification
                .route("service-planification", r -> r
                        .path("/api/salles/**", "/api/creneaux/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("${PLANIFICATION_SERVICE_URL:http://localhost:8083}"))

                // Service Jury
                .route("service-jury", r -> r
                        .path("/api/jury/**", "/api/notes/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("${JURY_SERVICE_URL:http://localhost:8084}"))

                // Service Résultats
                .route("service-resultats", r -> r
                        .path("/api/resultats/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("${RESULTATS_SERVICE_URL:http://localhost:8085}"))

                .build();
    }
}