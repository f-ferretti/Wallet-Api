package com.anteiku.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 * Provides API documentation settings using Swagger/OpenAPI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI bean for API documentation.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI walletOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API")
                        .description("API REST per la gestione di un wallet "
                                + "personale con transazioni di entrate e uscite")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Anteiku")
                                .email("support@anteiku.com")));
    }
}
