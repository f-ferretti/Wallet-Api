package com.anteiku.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API")
                        .description("API REST per la gestione di un wallet personale con transazioni di entrate e uscite")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Anteiku")
                                .email("support@anteiku.com")));
    }
}
