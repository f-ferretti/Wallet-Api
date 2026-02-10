package com.anteiku.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category;  // e.g. "Cibo", "Affitto", "Divertimento"

    @NotBlank(message = "Description is required")
    private String description;

    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    // INCOME or EXPENSE — determines sign for balance
    @NotNull(message = "Type is required")
    @Builder.Default
    private TransactionType type = TransactionType.EXPENSE;

    public enum TransactionType {
        INCOME, EXPENSE
    }
}