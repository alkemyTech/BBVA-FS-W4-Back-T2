package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {
    @Valid
    @NotBlank
    @Schema(description = "Tipo de moneda de la cuenta", example = "USD", required = true)
    private String currency;

    @Valid
    @NotBlank
    @Schema(description = "Tipo de cuenta", example = "AHORROS", required = true)
    private String accountType;
}
