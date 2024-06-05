package AlkemyWallet.AlkemyWallet.dtos;

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
    private Double amount;

    @NotNull
    @NotBlank
    private String creationDate;

    @NotBlank
    @NotNull
    private String closingDate;

}
