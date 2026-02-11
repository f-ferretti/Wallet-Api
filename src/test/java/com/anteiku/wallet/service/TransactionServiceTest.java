package com.anteiku.wallet.service;

import com.anteiku.wallet.model.Transaction;
import com.anteiku.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
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
                .date(LocalDateTime.now())
                .build();

        expenseTransaction = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("50.00"))
                .category("Cibo")
                .description("Spesa al supermercato")
                .type(Transaction.TransactionType.EXPENSE)
                .date(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Dovrebbe aggiungere una transazione con successo")
    void shouldAddTransaction() {
        // Given
        when(transactionRepository.save(any(Transaction.class))).thenReturn(incomeTransaction);

        // When
        Transaction result = transactionService.addTransaction(incomeTransaction);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.getType()).isEqualTo(Transaction.TransactionType.INCOME);
        verify(transactionRepository, times(1)).save(incomeTransaction);
    }

    @Test
    @DisplayName("Dovrebbe restituire tutte le transazioni")
    void shouldGetAllTransactions() {
        // Given
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getAllTransactions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(incomeTransaction, expenseTransaction);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire lista vuota quando non ci sono transazioni")
    void shouldReturnEmptyListWhenNoTransactions() {
        // Given
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Transaction> result = transactionService.getAllTransactions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare il saldo correttamente con entrate e uscite")
    void shouldCalculateBalanceCorrectly() {
        // Given
        Transaction income1 = Transaction.builder()
                .amount(new BigDecimal("1000.00"))
                .type(Transaction.TransactionType.INCOME)
                .build();

        Transaction income2 = Transaction.builder()
                .amount(new BigDecimal("500.00"))
                .type(Transaction.TransactionType.INCOME)
                .build();

        Transaction expense1 = Transaction.builder()
                .amount(new BigDecimal("200.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        Transaction expense2 = Transaction.builder()
                .amount(new BigDecimal("150.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        List<Transaction> transactions = Arrays.asList(income1, income2, expense1, expense2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        BigDecimal balance = transactionService.getBalance();

        // Then
        // Balance = (1000 + 500) - (200 + 150) = 1150
        assertThat(balance).isEqualByComparingTo(new BigDecimal("1150.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire saldo zero quando non ci sono transazioni")
    void shouldReturnZeroBalanceWhenNoTransactions() {
        // Given
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        BigDecimal balance = transactionService.getBalance();

        // Then
        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo negativo quando le uscite superano le entrate")
    void shouldCalculateNegativeBalance() {
        // Given
        Transaction income = Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .type(Transaction.TransactionType.INCOME)
                .build();

        Transaction expense = Transaction.builder()
                .amount(new BigDecimal("300.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        List<Transaction> transactions = Arrays.asList(income, expense);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        BigDecimal balance = transactionService.getBalance();

        // Then
        // Balance = 100 - 300 = -200
        assertThat(balance).isEqualByComparingTo(new BigDecimal("-200.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo correttamente con solo entrate")
    void shouldCalculateBalanceWithOnlyIncome() {
        // Given
        Transaction income1 = Transaction.builder()
                .amount(new BigDecimal("500.00"))
                .type(Transaction.TransactionType.INCOME)
                .build();

        Transaction income2 = Transaction.builder()
                .amount(new BigDecimal("300.00"))
                .type(Transaction.TransactionType.INCOME)
                .build();

        List<Transaction> transactions = Arrays.asList(income1, income2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        BigDecimal balance = transactionService.getBalance();

        // Then
        assertThat(balance).isEqualByComparingTo(new BigDecimal("800.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo correttamente con solo uscite")
    void shouldCalculateBalanceWithOnlyExpenses() {
        // Given
        Transaction expense1 = Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        Transaction expense2 = Transaction.builder()
                .amount(new BigDecimal("50.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .build();

        List<Transaction> transactions = Arrays.asList(expense1, expense2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        BigDecimal balance = transactionService.getBalance();

        // Then
        assertThat(balance).isEqualByComparingTo(new BigDecimal("-150.00"));
        verify(transactionRepository, times(1)).findAll();
    }
}
