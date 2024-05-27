package AlkemyWallet.AlkemyWallet.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionDTO {

    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String CBUDestino;

    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String CBUOrigen;

    @NotNull
    @NotBlank
    Double amount;

    @NotNull
    @NotBlank
    String currency;
}
