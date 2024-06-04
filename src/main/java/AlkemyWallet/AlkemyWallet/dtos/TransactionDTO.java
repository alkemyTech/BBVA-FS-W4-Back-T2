package AlkemyWallet.AlkemyWallet.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionDTO {
    @Schema(description = "CBU destino a la que transferimos", required = true)
    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String destino;

    @Schema(description = "Cantidad de dinero enviado", required = true)
    @NotNull
    @NotBlank
    Double amount;

    @Schema(description = "Moneda de la Transaccion", required = true)
    @NotNull
    @NotBlank
    String currency;
}
