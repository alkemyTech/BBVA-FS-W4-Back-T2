package AlkemyWallet.AlkemyWallet.dtos;


import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDto {
    @NotBlank
    @NotNull
    @Schema(description = "ID de la cuenta", example = "1", required = true)
    private Long id;
    @NotNull
    @NotBlank
    @Schema(description = "Moneda de la cuenta", example = "USD", required = true)
    private CurrencyEnum currency;
    @NotNull
    @NotBlank
    @Schema(description = "Tipo de cuenta", example = "AHORROS", required = true)
    private AccountTypeEnum accountType;
    @NotNull
    @NotBlank
    @Schema(description = "Límite de transacción de la cuenta", example = "1000.00", required = true)
    private Double transactionLimit;
    @NotNull
    @NotBlank
    @Schema(description = "Saldo de la cuenta", example = "5000.00", required = true)
    private Double balance;
    @NotNull
    @NotBlank
    @Schema(description = "CBU de la cuenta", example = "1234567890123456789012", required = true)
    private String CBU;
    @NotNull
    @NotBlank
    @Schema(description = "ID del usuario asociado a la cuenta", example = "1", required = true)
    private Long userId;
}