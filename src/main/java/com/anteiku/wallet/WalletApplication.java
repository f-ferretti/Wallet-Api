package com.anteiku.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Wallet application.
 * This is the entry point of the Spring Boot application.
 */
@SpringBootApplication
public class WalletApplication {

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

}
