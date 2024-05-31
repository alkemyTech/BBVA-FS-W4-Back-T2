package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import lombok.Data;

import java.util.List;

@Data
public class BalanceDTO {
    private Double accountArs;
    private Double accountUsd;
    private List<FixedTermDeposit> fixedTerms;
    private List<TransactionBalanceDTO> accountTransactions;
}
