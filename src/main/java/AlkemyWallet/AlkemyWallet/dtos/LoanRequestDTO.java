package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequestDTO {

    @NotNull
     Double amount;

    @NotNull
     Integer months;

}
