package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermDepositBalanceDTO {

    @NotNull
    @NotBlank
    @Schema(description = "Monto del plazo fijo", example = "5000.00", required = true)
    private Double amount;

    @NotNull
    @NotBlank
    @Schema(description = "Fecha de creación del plazo fijo", example = "2024-01-01", required = true)
    private String creationDate;

    @NotBlank
    @NotNull
    @Schema(description = "Fecha de cierre del plazo fijo", example = "2024-06-01", required = true)
    private String closingDate;

}
