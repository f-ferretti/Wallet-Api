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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
        when(transactionRepository.save(any(Transaction.class))).thenReturn(incomeTransaction);

        Transaction result = transactionService.addTransaction(incomeTransaction);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.getType()).isEqualTo(Transaction.TransactionType.INCOME);
        verify(transactionRepository, times(1)).save(incomeTransaction);
    }

    @Test
    @DisplayName("Dovrebbe restituire tutte le transazioni")
    void shouldGetAllTransactions() {
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(incomeTransaction, expenseTransaction);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire lista vuota quando non ci sono transazioni")
    void shouldReturnEmptyListWhenNoTransactions() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactions();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare il saldo correttamente con entrate e uscite")
    void shouldCalculateBalanceCorrectly() {
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

        BigDecimal balance = transactionService.getBalance();

        assertThat(balance).isEqualByComparingTo(new BigDecimal("1150.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire saldo zero quando non ci sono transazioni")
    void shouldReturnZeroBalanceWhenNoTransactions() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        BigDecimal balance = transactionService.getBalance();

        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo negativo quando le uscite superano le entrate")
    void shouldCalculateNegativeBalance() {
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

        BigDecimal balance = transactionService.getBalance();

        assertThat(balance).isEqualByComparingTo(new BigDecimal("-200.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo correttamente con solo entrate")
    void shouldCalculateBalanceWithOnlyIncome() {
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

        BigDecimal balance = transactionService.getBalance();

        assertThat(balance).isEqualByComparingTo(new BigDecimal("800.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe calcolare saldo correttamente con solo uscite")
    void shouldCalculateBalanceWithOnlyExpenses() {
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

        BigDecimal balance = transactionService.getBalance();

        assertThat(balance).isEqualByComparingTo(new BigDecimal("-150.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe trovare una transazione per ID")
    void shouldGetTransactionById() {
        when(transactionRepository.findById("1")).thenReturn(Optional.of(incomeTransaction));

        Optional<Transaction> result = transactionService.getTransactionById("1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
        assertThat(result.get().getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(transactionRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("Dovrebbe restituire Optional vuoto quando transazione non esiste")
    void shouldReturnEmptyWhenTransactionNotFound() {
        when(transactionRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransactionById("999");

        assertThat(result).isEmpty();
        verify(transactionRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Dovrebbe eliminare una transazione")
    void shouldDeleteTransaction() {
        doNothing().when(transactionRepository).deleteById("1");

        transactionService.deleteTransaction("1");

        verify(transactionRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Dovrebbe aggiornare una transazione esistente")
    void shouldUpdateTransaction() {
        Transaction updatedTransaction = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("1500.00"))
                .category("Stipendio")
                .description("Stipendio mensile aggiornato")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction result = transactionService.updateTransaction("1", updatedTransaction);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(result.getDescription()).isEqualTo("Stipendio mensile aggiornato");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni per tipo INCOME")
    void shouldFilterTransactionsByTypeIncome() {
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                Transaction.TransactionType.INCOME, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(Transaction.TransactionType.INCOME);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni per tipo EXPENSE")
    void shouldFilterTransactionsByTypeExpense() {
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                Transaction.TransactionType.EXPENSE, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(Transaction.TransactionType.EXPENSE);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni per periodo")
    void shouldFilterTransactionsByDateRange() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);

        Transaction pastTransaction = Transaction.builder()
                .id("3")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("Past")
                .type(Transaction.TransactionType.INCOME)
                .date(yesterday)
                .build();

        Transaction currentTransaction = Transaction.builder()
                .id("4")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("Current")
                .type(Transaction.TransactionType.EXPENSE)
                .date(today)
                .build();

        List<Transaction> transactions = Arrays.asList(pastTransaction, currentTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                null, today.toLocalDate(), tomorrow.toLocalDate());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("4");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni per tipo e periodo")
    void shouldFilterTransactionsByTypeAndDateRange() {
        LocalDateTime today = LocalDateTime.now();

        Transaction income1 = Transaction.builder()
                .amount(new BigDecimal("1000.00"))
                .type(Transaction.TransactionType.INCOME)
                .date(today.minusDays(2))
                .build();

        Transaction income2 = Transaction.builder()
                .amount(new BigDecimal("500.00"))
                .type(Transaction.TransactionType.INCOME)
                .date(today)
                .build();

        Transaction expense = Transaction.builder()
                .amount(new BigDecimal("200.00"))
                .type(Transaction.TransactionType.EXPENSE)
                .date(today)
                .build();

        List<Transaction> transactions = Arrays.asList(income1, income2, expense);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                Transaction.TransactionType.INCOME,
                today.toLocalDate().minusDays(1),
                today.toLocalDate());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire tutte le transazioni quando tutti i filtri sono null")
    void shouldReturnAllTransactionsWhenAllFiltersAreNull() {
        List<Transaction> transactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(null, null, null);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(incomeTransaction, expenseTransaction);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni con solo startDate (endDate null)")
    void shouldFilterTransactionsWithOnlyStartDate() {
        LocalDateTime baseDate = LocalDateTime.of(2024, 1, 15, 10, 0);

        Transaction beforeStart = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("Before start")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate.minusDays(5))
                .build();

        Transaction afterStart = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("After start")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate.plusDays(5))
                .build();

        Transaction onStartDate = Transaction.builder()
                .id("3")
                .amount(new BigDecimal("300.00"))
                .category("Test")
                .description("On start date")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate)
                .build();

        List<Transaction> transactions = Arrays.asList(beforeStart, afterStart, onStartDate);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                null,
                LocalDate.of(2024, 1, 15),
                null
        );

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Transaction::getId)
                .containsExactlyInAnyOrder("2", "3");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni con solo endDate (startDate null)")
    void shouldFilterTransactionsWithOnlyEndDate() {
        LocalDateTime baseDate = LocalDateTime.of(2024, 1, 15, 10, 0);

        Transaction beforeEnd = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("Before end")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate.minusDays(5))
                .build();

        Transaction afterEnd = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("After end")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate.plusDays(5))
                .build();

        Transaction onEndDate = Transaction.builder()
                .id("3")
                .amount(new BigDecimal("300.00"))
                .category("Test")
                .description("On end date")
                .type(Transaction.TransactionType.INCOME)
                .date(baseDate)
                .build();

        List<Transaction> transactions = Arrays.asList(beforeEnd, afterEnd, onEndDate);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                null,
                null,
                LocalDate.of(2024, 1, 15)
        );

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Transaction::getId)
                .containsExactlyInAnyOrder("1", "3");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni all'inizio del range (boundary test)")
    void shouldFilterTransactionsAtStartBoundary() {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        LocalDateTime exactStart = testDate.atStartOfDay();

        Transaction atExactStart = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("At exact start")
                .type(Transaction.TransactionType.INCOME)
                .date(exactStart)
                .build();

        Transaction oneSecondBefore = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("One second before")
                .type(Transaction.TransactionType.INCOME)
                .date(exactStart.minusSeconds(1))
                .build();

        List<Transaction> transactions = Arrays.asList(atExactStart, oneSecondBefore);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                null,
                testDate,
                testDate.plusDays(1)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare transazioni alla fine del range (boundary test)")
    void shouldFilterTransactionsAtEndBoundary() {
        LocalDate testDate = LocalDate.of(2024, 1, 15);

        Transaction atExactEnd = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("At exact end")
                .type(Transaction.TransactionType.INCOME)
                .date(testDate.atTime(23, 59, 59))
                .build();

        Transaction oneSecondAfter = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("One second after")
                .type(Transaction.TransactionType.INCOME)
                .date(testDate.plusDays(1).atStartOfDay())
                .build();

        List<Transaction> transactions = Arrays.asList(atExactEnd, oneSecondAfter);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                null,
                testDate.minusDays(1),
                testDate
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare per tipo con solo startDate")
    void shouldFilterByTypeWithOnlyStartDate() {
        LocalDate startDate = LocalDate.of(2024, 1, 10);

        Transaction oldIncome = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("Old income")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.of(2024, 1, 5, 10, 0))
                .build();

        Transaction newIncome = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("New income")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        Transaction newExpense = Transaction.builder()
                .id("3")
                .amount(new BigDecimal("50.00"))
                .category("Test")
                .description("New expense")
                .type(Transaction.TransactionType.EXPENSE)
                .date(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        List<Transaction> transactions = Arrays.asList(oldIncome, newIncome, newExpense);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                Transaction.TransactionType.INCOME,
                startDate,
                null
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("2");
        assertThat(result.get(0).getType()).isEqualTo(Transaction.TransactionType.INCOME);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe filtrare per tipo con solo endDate")
    void shouldFilterByTypeWithOnlyEndDate() {
        LocalDate endDate = LocalDate.of(2024, 1, 15);

        Transaction oldExpense = Transaction.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .category("Test")
                .description("Old expense")
                .type(Transaction.TransactionType.EXPENSE)
                .date(LocalDateTime.of(2024, 1, 10, 10, 0))
                .build();

        Transaction newExpense = Transaction.builder()
                .id("2")
                .amount(new BigDecimal("200.00"))
                .category("Test")
                .description("New expense")
                .type(Transaction.TransactionType.EXPENSE)
                .date(LocalDateTime.of(2024, 1, 20, 10, 0))
                .build();

        Transaction oldIncome = Transaction.builder()
                .id("3")
                .amount(new BigDecimal("50.00"))
                .category("Test")
                .description("Old income")
                .type(Transaction.TransactionType.INCOME)
                .date(LocalDateTime.of(2024, 1, 10, 10, 0))
                .build();

        List<Transaction> transactions = Arrays.asList(oldExpense, newExpense, oldIncome);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.filterTransactions(
                Transaction.TransactionType.EXPENSE,
                null,
                endDate
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(0).getType()).isEqualTo(Transaction.TransactionType.EXPENSE);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire summary completo con entrate, uscite e saldo")
    void shouldGetCompleteSummary() {
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

        Map<String, BigDecimal> summary = transactionService.getSummary();

        assertThat(summary).containsKeys("totalIncome", "totalExpense", "balance");
        assertThat(summary.get("totalIncome")).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(summary.get("totalExpense")).isEqualByComparingTo(new BigDecimal("350.00"));
        assertThat(summary.get("balance")).isEqualByComparingTo(new BigDecimal("1150.00"));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Dovrebbe restituire summary con valori zero quando non ci sono transazioni")
    void shouldReturnZeroSummaryWhenNoTransactions() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        Map<String, BigDecimal> summary = transactionService.getSummary();

        assertThat(summary).containsKeys("totalIncome", "totalExpense", "balance");
        assertThat(summary.get("totalIncome")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.get("totalExpense")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.get("balance")).isEqualByComparingTo(BigDecimal.ZERO);
        verify(transactionRepository, times(1)).findAll();
    }
}