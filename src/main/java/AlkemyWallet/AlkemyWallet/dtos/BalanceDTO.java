package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BalanceDTO {
    private Double accountArs;
    private Double accountUsd;
    private List<FixedTermDeposit> fixedTerms;
    private Map<Long, List<TransactionBalanceDTO>> accountTransactions; // Nuevo campo para transacciones por cuenta
}
