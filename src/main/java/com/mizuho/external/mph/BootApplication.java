package com.mizuho.external.mph;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.mizuho.external.mph.scheduling.StalePriceProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableScheduling
@ComponentScan
@EnableConfigurationProperties(StalePriceProperties.class)
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BootApplication.class);
        application.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event ->
                requireActiveProfile(event.getEnvironment()));
        application.run(args);
    }

    static void requireActiveProfile(Environment environment) {
        if (environment.getActiveProfiles().length == 0) {
            throw new IllegalStateException(
                    "No Spring profile is active. Set --spring.profiles.active=<profile> or SPRING_PROFILES_ACTIVE.");
        }
    }

    @Bean
    public OpenAPI marketPriceHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MarketPriceHub API")
                        .description("Service for managing market prices per instruments & vendors")
                        .version("1.0"));
    }
}
