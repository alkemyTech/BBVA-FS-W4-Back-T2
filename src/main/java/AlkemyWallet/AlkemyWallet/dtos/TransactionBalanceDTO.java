package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TransactionBalanceDTO {

        @Schema(description = "ID de la transacción", example = "1", required = true)
        @NotNull
        private Long id;

        @Schema(description = "Cantidad de dinero enviado o recibido", example = "100.00", required = true)
        @NotNull
        private Double amount;

        @Schema(description = "Fecha de la transacción realizada", example = "2024-06-01T12:30:00", required = true)
        @NotNull
        private LocalDateTime transactionDate;

        @Schema(description = "Detalle de la transacción realizada", example = "Pago de servicios")
        private String description;

        @Schema(description = "Tipo de transacción", example = "DEBITO", required = true)
        @NotNull
        private TransactionEnum type;

        @Schema(description = "Moneda de la transacción", example = "ARS", required = true)
        @NotNull
        private String currency;

        @Schema(description = "CBU de la cuenta de origen", example = "1234567890123456789012", required = true)
        @NotNull
        private String originAccountCBU;


    }
