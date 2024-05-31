package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    @NotNull
    @NotBlank
    @Size(min=22, max=22)
    String destino;

    String origen;

    LocalDate fechaDeTransaccion;

    TransactionEnum tipoDeTransaccion;

    @NotNull
    @NotBlank
    String currency;
}
