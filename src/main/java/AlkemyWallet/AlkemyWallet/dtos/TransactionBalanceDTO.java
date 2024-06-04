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

        @Schema(description = "Id de la transaccion", required = true)
        @NotNull
        private Long id;

        @Schema(description = "Cantidad de dinero enviado o recibido", required = true)
        @NotNull
        private Double amount;

        @Schema(description = "Fecha de la transaccion realizada", required = true)
        @NotNull
        private LocalDateTime transactionDate;

        @Schema(description = "Detalle de la transaccion realizada", required = false)
        private String description;

        @Schema(description = "Tipo de Transaccion", required = true)
        @NotNull
        private TransactionEnum type;

        @Schema(description = "Moneda de la Transaccion", required = true)
        @NotNull
        private String currency;


        @NotNull
        private String originAccountCBU;


    }
