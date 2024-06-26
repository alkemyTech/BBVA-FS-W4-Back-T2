package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    @NotNull
    String fechaPago;

    @Schema(description = "Moneda de la Transacción", required = true)
    @NotNull
    @NotBlank
    String currency;

    @Schema(description = "CBU destino a la que transferimos", required = true)
    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String destino;

    @Schema(description = "Cantidad de dinero enviado", required = true)
    @NotNull
    @Positive
    Double amount;

    String description;


}
