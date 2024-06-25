package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LoanResponseDTO {


    @NotNull
    @Schema(description = "Monto original del préstamo", example = "10000", required = true)
    private Double originalAmount;

    @NotNull
    @Schema(description = "Monto total a pagar por el préstamo", example = "12000", required = true)
    private Double totalAmount;

    @NotNull
    @Schema(description = "Número de cuotas del préstamo", example = "12", required = true)
    private Integer months;

    @NotNull
    @Schema(description = "Monto mensual a pagar", example = "1000", required = true)
    private Double monthlyAmount;

    @NotNull
    @Schema(description = "Porcentaje de interés aplicado", example = "20", required = true)
    private Double interestPercentage;


}