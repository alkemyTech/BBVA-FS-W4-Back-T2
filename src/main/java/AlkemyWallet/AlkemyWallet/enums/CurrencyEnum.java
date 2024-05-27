package AlkemyWallet.AlkemyWallet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrencyEnum {
    ARS(300000.00),
    USD(1000.00);

    private double transactionLimit;

}
