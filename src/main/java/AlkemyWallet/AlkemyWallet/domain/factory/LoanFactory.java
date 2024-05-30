package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.config.LoanConfig;
import AlkemyWallet.AlkemyWallet.domain.Loan;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class LoanFactory {


    public Loan createLoan(Double amount, Integer months, Double monthlyAmount, Double totalAmount, Double interestPercentage) {
        return new Loan(null, amount, totalAmount, months, monthlyAmount,interestPercentage);

    };
}
