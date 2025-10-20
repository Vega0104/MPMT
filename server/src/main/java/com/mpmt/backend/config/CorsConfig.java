////package com.mpmt.backend.config;
////
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.web.servlet.config.annotation.CorsRegistry;
////import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
////
////@Configuration
////public class CorsConfig {
////    @Bean
////    public WebMvcConfigurer corsConfigurer() {
////        return new WebMvcConfigurer() {
////            @Override
////            public void addCorsMappings(CorsRegistry registry) {
////                registry.addMapping("/**")
////                        .allowedOrigins(
////                        "http://localhost:8080",
////                        "http://127.0.0.1:8080" )
////                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
////                        .allowedHeaders("*")
////                        .allowCredentials(true);
////            }
////        };
////    }
////}
//
//
//// src/main/java/com/mpmt/backend/config/CorsConfig.java
//package com.mpmt.backend.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.List;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
//        CorsConfiguration cfg = new CorsConfiguration();
//        cfg.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:4200"));
//        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//        cfg.setAllowedHeaders(List.of("*"));      // authorization, content-type, etc.
//        cfg.setExposedHeaders(List.of("*"));
//        cfg.setAllowCredentials(true);
//        cfg.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", cfg);
//
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // ⬅️ très important: AVANT JwtAuthFilter
//        return bean;
//    }
//}
//


package com.mpmt.backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Origines autorisées (ajoutez toutes les origines nécessaires)
        cfg.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:4200",
                "http://127.0.0.1:8080",
                "http://127.0.0.1:4200"
        ));

        // Méthodes HTTP autorisées
        cfg.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));

        // Headers autorisés (tous les headers)
        cfg.setAllowedHeaders(Arrays.asList("*"));

        // Headers exposés au client JavaScript
        cfg.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "remember-me",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"
        ));

        // Autoriser l'envoi de credentials (cookies, authorization headers)
        cfg.setAllowCredentials(true);

        // Durée de cache pour les requêtes preflight (1 heure)
        cfg.setMaxAge(3600L);

        // Configuration de la source
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        // Création du filtre avec la plus haute priorité
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // CRUCIAL: doit s'exécuter AVANT JwtAuthFilter

        return bean;
    }
}