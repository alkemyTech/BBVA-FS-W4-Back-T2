package AlkemyWallet.AlkemyWallet.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    @Schema(description = "CBU destino a la que transferimos", required = true)
    @NumberFormat
    @Size(min=22, max=22)
    String destino;

    @Schema(description = "Cantidad de dinero enviado", required = true)
    @NotNull
    Double amount;

    @Schema(description = "Moneda de la Transacción", required = true)
    @NotNull
    @NotBlank
    String currency;

    String description;
}
