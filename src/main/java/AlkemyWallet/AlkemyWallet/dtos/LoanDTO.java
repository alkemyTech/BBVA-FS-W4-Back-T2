package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class LoanDTO {

    @NotNull
     Double amount;

    @NotNull
     Integer months;

}
