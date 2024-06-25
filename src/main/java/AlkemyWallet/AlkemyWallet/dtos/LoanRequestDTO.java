package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequestDTO {

    @NotNull
    @Schema(description = "Monto del préstamo", example = "10000", required = true)
    Double amount;

    @NotNull
    @Schema(description = "Número de cuotas del préstamo", example = "12", required = true)
    Integer months;

}
