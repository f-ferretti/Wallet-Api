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
import java.time.LocalDate;
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

    @GetMapping("/transactions/{id}")
    @Operation(summary = "Ottieni una transazione", description = "Restituisce i dettagli di una transazione specifica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transazione trovata"),
        @ApiResponse(responseCode = "404", description = "Transazione non trovata")
    })
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/transactions/{id}")
    @Operation(summary = "Elimina una transazione", description = "Elimina una transazione esistente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transazione eliminata"),
        @ApiResponse(responseCode = "404", description = "Transazione non trovata")
    })
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transactions/{id}")
    @Operation(summary = "Aggiorna una transazione", description = "Modifica una transazione esistente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transazione aggiornata"),
        @ApiResponse(responseCode = "400", description = "Dati non validi"),
        @ApiResponse(responseCode = "404", description = "Transazione non trovata")
    })
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable String id,
            @Valid @RequestBody Transaction transaction) {
        Transaction updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/transactions/filter")
    @Operation(summary = "Filtra le transazioni", description = "Filtra le transazioni per tipo e/o periodo")
    @ApiResponse(responseCode = "200", description = "Lista delle transazioni filtrate")
    public ResponseEntity<List<Transaction>> filterTransactions(
            @RequestParam(required = false) Transaction.TransactionType type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        List<Transaction> filtered = transactionService.filterTransactions(type, startDate, endDate);
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/summary")
    @Operation(summary = "Ottieni statistiche", description = "Restituisce totale entrate, uscite e saldo")
    @ApiResponse(responseCode = "200", description = "Statistiche del wallet")
    public ResponseEntity<Map<String, BigDecimal>> getSummary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }
}
