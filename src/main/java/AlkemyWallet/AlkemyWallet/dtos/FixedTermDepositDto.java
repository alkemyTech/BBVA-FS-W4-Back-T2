package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
//NECESITO CREAR MAS DE UN TIPO DE CONSTRUCTOR ASI PUEDO ENVIAR COSAS PARCIALES
@Data
public class FixedTermDepositDto {

    @NotBlank
    @NotNull
    private LocalDateTime creationDate;

    @NotBlank
    @NotNull
    private LocalDateTime closingDate;

    @NotBlank
    @NotNull
    private double invertedAmount;

    @NotBlank
    @NotNull
    private double gainedInterest;

    @NotBlank
    @NotNull
    private double totalAmountToCollect;

}
