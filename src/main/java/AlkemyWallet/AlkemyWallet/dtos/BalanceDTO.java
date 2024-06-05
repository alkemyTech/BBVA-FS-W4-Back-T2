package AlkemyWallet.AlkemyWallet.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BalanceDTO {
    private Double accountArs;
    private Double accountUsd;
    private List<FixedTermDepositBalanceDTO> fixedTerms;
    private List<TransactionBalanceDTO> accountTransactions;
}
