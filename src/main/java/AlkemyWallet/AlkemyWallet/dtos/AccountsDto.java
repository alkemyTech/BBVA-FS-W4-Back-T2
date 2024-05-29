package AlkemyWallet.AlkemyWallet.dtos;


import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDto {
    @NotBlank
    @NotNull
    private long id;
    @NotNull
    @NotBlank
    private CurrencyEnum currency;
    @NotNull
    @NotBlank
    private Double transactionLimit;
    @NotNull
    @NotBlank
    private Double balance;
    @NotNull
    @NotBlank
    private String CBU;
    @NotNull
    @NotBlank
    private User userId;
}
