package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionBalanceDTO {

    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String destino;


    @NotNull
    @NotBlank
    Double amount;

    @NotNull
    @NotBlank
    String currency;

    @NotNull
    @NotBlank
    LocalDateTime transactionDate;

    @NotNull
    @NotBlank
    TransactionEnum type;

}
