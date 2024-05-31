package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
