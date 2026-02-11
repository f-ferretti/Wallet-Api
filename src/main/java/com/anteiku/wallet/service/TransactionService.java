package com.anteiku.wallet.service;

import com.anteiku.wallet.model.Transaction;
import com.anteiku.wallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * balance = sum(INCOME) - sum(EXPENSE)
     */
    public BigDecimal getBalance() {
        List<Transaction> all = transactionRepository.findAll();

        BigDecimal income = all.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = all.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return income.subtract(expense);
    }

    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepository.findById(id);
    }

    public void deleteTransaction(String id) {
        transactionRepository.deleteById(id);
    }

    public Transaction updateTransaction(String id, Transaction transaction) {
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> filterTransactions(Transaction.TransactionType type, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> {
                    if (startDate == null && endDate == null) return true;
                    LocalDateTime txDate = t.getDate();
                    if (startDate != null && endDate != null) {
                        LocalDateTime start = startDate.atStartOfDay();
                        LocalDateTime end = endDate.atTime(LocalTime.MAX);
                        return !txDate.isBefore(start) && !txDate.isAfter(end);
                    }
                    if (startDate != null) {
                        return !txDate.isBefore(startDate.atStartOfDay());
                    }
                    return !txDate.isAfter(endDate.atTime(LocalTime.MAX));
                })
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getSummary() {
        List<Transaction> all = transactionRepository.findAll();

        BigDecimal income = all.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = all.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = income.subtract(expense);

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", income);
        summary.put("totalExpense", expense);
        summary.put("balance", balance);

        return summary;
    }
}
