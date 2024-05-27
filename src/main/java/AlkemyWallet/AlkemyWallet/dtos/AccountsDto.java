package AlkemyWallet.AlkemyWallet.dtos;


import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDto {
    private long id;
    private CurrencyEnum currency;
    private Double transactionLimit;
    private Double balance;
    private String CBU;
    private long userId;
}