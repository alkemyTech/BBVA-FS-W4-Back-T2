package AlkemyWallet.AlkemyWallet.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionFilter {
    private Long userId;
    private int page;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String transactionType;
    private String currency;

}