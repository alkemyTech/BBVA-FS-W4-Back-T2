package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBalanceDTO {

    @NotNull
    private Long id;
    @NotNull
    private Double amount;
    @NotNull
    private LocalDateTime transactionDate;
    private String description;
    @NotNull
    private TransactionEnum type;
    @NotNull
    private String currency;
    @NotNull
    private String originAccountCBU;


}
