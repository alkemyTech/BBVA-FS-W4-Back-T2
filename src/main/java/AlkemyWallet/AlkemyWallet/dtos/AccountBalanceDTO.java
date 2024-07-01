package AlkemyWallet.AlkemyWallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private Long accountId;
    private String currency;
    private String accountType;
    private Double balance;
    private String CBU;
    private String alias;

}