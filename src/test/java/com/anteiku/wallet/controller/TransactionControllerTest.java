package com.anteiku.wallet.controller;

import com.anteiku.wallet.model.Transaction;
import com.anteiku.wallet.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private Transaction incomeTransaction;
    private Transaction expenseTransaction;

    @BeforeEach
    void setUp() {
        incomeTransaction = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("1000.00"))
                .category("Stipendio")
                .description("Stipendio mensile")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        expenseTransaction = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("50.00"))
                .category("Cibo")
                .description("Spesa al supermercato")
                .type(Transaction.TransactionType.EXPENSE)
                .date(LocalDateTime.of(2024, 1, 16, 14, 30))
                .build();
    }

    // ==================== POST /api/transactions ====================

    @Test
    @DisplayName("POST /api/transactions - Dovrebbe creare una transazione con successo")
    void shouldCreateTransactionSuccessfully() throws Exception {
        // Given
        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(incomeTransaction);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.category").value("Stipendio"))
                .andExpect(jsonPath("$.description").value("Stipendio mensile"))
                .andExpect(jsonPath("$.type").value("INCOME"));

        verify(transactionService, times(1)).addTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("POST /api/transactions - Dovrebbe restituire 400 con amount negativo")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        // Given
        Transaction invalidTransaction = Transaction.builder()
                .amount(new BigDecimal("-100.00"))
                .category("Test")
                .description("Invalid amount")
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).addTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("POST /api/transactions - Dovrebbe restituire 400 con category vuota")
    void shouldReturn400WhenCategoryIsEmpty() throws Exception {
        // Given
        Transaction invalidTransaction = Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .category("")
                .description("Test")
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).addTransaction(any(Transaction.class));
    }

    // ==================== GET /api/transactions ====================

    @Test
    @DisplayName("GET /api/transactions - Dovrebbe restituire tutte le transazioni")
    void shouldGetAllTransactions() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].amount").value(1000.00))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].amount").value(50.00));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    @DisplayName("GET /api/transactions - Dovrebbe restituire lista vuota")
    void shouldReturnEmptyListWhenNoTransactions() throws Exception {
        // Given
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(transactionService, times(1)).getAllTransactions();
    }

    // ==================== GET /api/transactions/{id} ====================

    @Test
    @DisplayName("GET /api/transactions/{id} - Dovrebbe restituire la transazione trovata")
    void shouldGetTransactionById() throws Exception {
        // Given
        when(transactionService.getTransactionById("1")).thenReturn(Optional.of(incomeTransaction));

        // When & Then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.category").value("Stipendio"));

        verify(transactionService, times(1)).getTransactionById("1");
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Dovrebbe restituire 404 se non trovata")
    void shouldReturn404WhenTransactionNotFound() throws Exception {
        // Given
        when(transactionService.getTransactionById("999")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());

        verify(transactionService, times(1)).getTransactionById("999");
    }

    // ==================== GET /api/balance ====================

    @Test
    @DisplayName("GET /api/balance - Dovrebbe restituire il saldo corrente")
    void shouldGetBalance() throws Exception {
        // Given
        BigDecimal balance = new BigDecimal("950.00");
        when(transactionService.getBalance()).thenReturn(balance);

        // When & Then
        mockMvc.perform(get("/api/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(950.00));

        verify(transactionService, times(1)).getBalance();
    }

    @Test
    @DisplayName("GET /api/balance - Dovrebbe restituire saldo zero")
    void shouldReturnZeroBalance() throws Exception {
        // Given
        when(transactionService.getBalance()).thenReturn(BigDecimal.ZERO);

        // When & Then
        mockMvc.perform(get("/api/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0));

        verify(transactionService, times(1)).getBalance();
    }

    @Test
    @DisplayName("GET /api/balance - Dovrebbe restituire saldo negativo")
    void shouldReturnNegativeBalance() throws Exception {
        // Given
        BigDecimal negativeBalance = new BigDecimal("-250.00");
        when(transactionService.getBalance()).thenReturn(negativeBalance);

        // When & Then
        mockMvc.perform(get("/api/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(-250.00));

        verify(transactionService, times(1)).getBalance();
    }

    // ==================== DELETE /api/transactions/{id} ====================

    @Test
    @DisplayName("DELETE /api/transactions/{id} - Dovrebbe eliminare la transazione")
    void shouldDeleteTransaction() throws Exception {
        // Given
        doNothing().when(transactionService).deleteTransaction("1");

        // When & Then
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction("1");
    }

    // ==================== PUT /api/transactions/{id} ====================

    @Test
    @DisplayName("PUT /api/transactions/{id} - Dovrebbe aggiornare la transazione")
    void shouldUpdateTransaction() throws Exception {
        // Given
        Transaction updatedTransaction = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("1500.00"))
                .category("Stipendio")
                .description("Stipendio aggiornato")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.now())
                .build();

        when(transactionService.updateTransaction(eq("1"), any(Transaction.class)))
                .thenReturn(updatedTransaction);

        // When & Then
        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.description").value("Stipendio aggiornato"));

        verify(transactionService, times(1)).updateTransaction(eq("1"), any(Transaction.class));
    }

    @Test
    @DisplayName("PUT /api/transactions/{id} - Dovrebbe restituire 400 con dati non validi")
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        // Given
        Transaction invalidTransaction = Transaction.builder()
                .amount(new BigDecimal("-100.00"))
                .category("")
                .description("Invalid")
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        // When & Then
        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).updateTransaction(anyString(), any(Transaction.class));
    }

    // ==================== GET /api/health ====================

    @Test
    @DisplayName("GET /api/health - Dovrebbe restituire OK")
    void shouldReturnHealthOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    // ==================== GET /api/transactions/filter ====================

    @Test
    @DisplayName("GET /api/transactions/filter - Dovrebbe filtrare per tipo EXPENSE")
    void shouldFilterTransactionsByType() throws Exception {
        // Given
        List<Transaction> expenses = Collections.singletonList(expenseTransaction);
        when(transactionService.filterTransactions(
                eq(Transaction.TransactionType.EXPENSE),
                any(),
                any()
        )).thenReturn(expenses);

        // When & Then
        mockMvc.perform(get("/api/transactions/filter")
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"));

        verify(transactionService, times(1)).filterTransactions(
                eq(Transaction.TransactionType.EXPENSE),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("GET /api/transactions/filter - Dovrebbe filtrare per date range")
    void shouldFilterTransactionsByDateRange() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(transactionService.filterTransactions(
                any(),
                eq(startDate),
                eq(endDate)
        )).thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/transactions/filter")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(transactionService, times(1)).filterTransactions(
                any(),
                eq(startDate),
                eq(endDate)
        );
    }

    @Test
    @DisplayName("GET /api/transactions/filter - Dovrebbe restituire lista vuota se nessun match")
    void shouldReturnEmptyListWhenNoMatchingTransactions() throws Exception {
        // Given
        when(transactionService.filterTransactions(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/transactions/filter")
                        .param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(transactionService, times(1)).filterTransactions(any(), any(), any());
    }

    // ==================== GET /api/summary ====================

    @Test
    @DisplayName("GET /api/summary - Dovrebbe restituire il riepilogo completo")
    void shouldGetSummary() throws Exception {
        // Given
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", new BigDecimal("1500.00"));
        summary.put("totalExpense", new BigDecimal("350.00"));
        summary.put("balance", new BigDecimal("1150.00"));

        when(transactionService.getSummary()).thenReturn(summary);

        // When & Then
        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(1500.00))
                .andExpect(jsonPath("$.totalExpense").value(350.00))
                .andExpect(jsonPath("$.balance").value(1150.00));

        verify(transactionService, times(1)).getSummary();
    }

    @Test
    @DisplayName("GET /api/summary - Dovrebbe restituire summary con valori zero")
    void shouldReturnZeroSummary() throws Exception {
        // Given
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", BigDecimal.ZERO);
        summary.put("totalExpense", BigDecimal.ZERO);
        summary.put("balance", BigDecimal.ZERO);

        when(transactionService.getSummary()).thenReturn(summary);

        // When & Then
        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(0))
                .andExpect(jsonPath("$.totalExpense").value(0))
                .andExpect(jsonPath("$.balance").value(0));

        verify(transactionService, times(1)).getSummary();
    }
}