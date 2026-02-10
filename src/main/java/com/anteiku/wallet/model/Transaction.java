package com.anteiku.wallet.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Modello di una transazione del wallet")
public class Transaction {

    @Id
    @Schema(description = "ID univoco della transazione", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Importo della transazione", example = "50.00", minimum = "0.01")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    @Schema(description = "Categoria della transazione", example = "Cibo")
    private String category;  // e.g. "Cibo", "Affitto", "Divertimento"

    @NotBlank(message = "Description is required")
    @Schema(description = "Descrizione della transazione", example = "Spesa al supermercato")
    private String description;

    @Builder.Default
    @Schema(description = "Data e ora della transazione", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime date = LocalDateTime.now();

    // INCOME or EXPENSE — determines sign for balance
    @NotNull(message = "Type is required")
    @Builder.Default
    @Schema(description = "Tipo di transazione", example = "EXPENSE")
    private TransactionType type = TransactionType.EXPENSE;

    @Schema(description = "Tipo di transazione: INCOME (entrata) o EXPENSE (uscita)")
    public enum TransactionType {
        INCOME, EXPENSE
    }
}