package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    @Schema(description = "CBU destino a la que transferimos", required = true)
    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String destino;

    @Schema(description = "CBU origen del que transferimos", required = true)
    @NotNull
    @NotBlank
    String origen;

    @Schema(description = "Fecha de la transaccion realizada", required = true)
    LocalDate fechaDeTransaccion;

    @Schema(description = "Tipo de Transaccion", required = true)
    TransactionEnum tipoDeTransaccion;

    @Schema(description = "Moneda de la Transaccion", required = true)
    @NotNull
    @NotBlank
    String currency;
}
