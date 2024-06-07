package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BalanceDTO {
    @Schema(description = "Saldo en cuenta en ARS", example = "15000.00")
    private Double accountArs;
    @Schema(description = "Saldo en cuenta en USD", example = "3000.00")
    private Double accountUsd;
    @Schema(description = "Lista de plazos fijos")
    private List<FixedTermDepositBalanceDTO> fixedTerms;
    @Schema(description = "Lista de transacciones de la cuenta")
    private List<TransactionBalanceDTO> accountTransactions;
}
