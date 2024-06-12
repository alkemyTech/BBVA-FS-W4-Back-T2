package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
//NECESITO CREAR MAS DE UN TIPO DE CONSTRUCTOR ASI PUEDO ENVIAR COSAS PARCIALES
@Data
public class FixedTermDepositDto {

    @NotBlank
    @NotNull
    @Schema(description = "Fecha de creación del plazo fijo", example = "2024-01-01", required = true)
    private String creationDate;

    @NotBlank
    @NotNull
    @Schema(description = "Fecha de cierre del plazo fijo", example = "2024-06-01", required = true)
    private String closingDate;

    @NotBlank
    @NotNull
    @Schema(description = "Monto invertido en el plazo fijo", example = "10000.00", required = true)
    private Double invertedAmount;

    @Schema(description = "Intereses ganados del plazo fijo", example = "500.00")
    private Double gainedInterest;

    @Schema(description = "Monto total a cobrar al vencimiento del plazo fijo", example = "10500.00")
    private Double totalAmountToCollect;

}
