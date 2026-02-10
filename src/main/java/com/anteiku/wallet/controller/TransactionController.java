package com.anteiku.wallet.controller;

import com.anteiku.wallet.model.Transaction;
import com.anteiku.wallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "API per la gestione del wallet personale")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    @Operation(summary = "Aggiungi una transazione", description = "Crea una nuova transazione (entrata o uscita)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transazione creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction saved = transactionService.addTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Ottieni tutte le transazioni", description = "Restituisce la lista completa delle transazioni")
    @ApiResponse(responseCode = "200", description = "Lista delle transazioni")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/balance")
    @Operation(summary = "Ottieni il saldo", description = "Calcola e restituisce il saldo corrente del wallet")
    @ApiResponse(responseCode = "200", description = "Saldo corrente")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        BigDecimal balance = transactionService.getBalance();
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica lo stato dell'API")
    @ApiResponse(responseCode = "200", description = "API funzionante")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
