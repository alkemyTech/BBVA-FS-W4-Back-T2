package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.LoanConfig;
import AlkemyWallet.AlkemyWallet.domain.Loan;
import AlkemyWallet.AlkemyWallet.domain.factory.LoanFactory;
import AlkemyWallet.AlkemyWallet.dtos.LoanRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoanResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanService {
    private final LoanFactory loanFactory;
    private final LoanConfig loanConfig;

    public LoanResponseDTO simulateLoan(LoanRequestDTO loanRequestDTO) {
        Integer months= loanRequestDTO.getMonths();
        Double amount= loanRequestDTO.getAmount();
        Double interestPercentage = loanConfig.getInterestMonthlyPercentage();
        Double interestMonthlyAmount = this.generateMonthlyAmount(interestPercentage, amount);

        return loanFactory.createLoanSimulation(
                amount,
                months,
                interestMonthlyAmount,
                this.generateTotalAmount(interestMonthlyAmount, months, amount),
                interestPercentage
        );


    }

    private Double generateTotalAmount(Double interestMonthlyAmount, Integer months, Double amount) {
            return interestMonthlyAmount * months + amount;
    }

    private Double generateMonthlyAmount(Double interestPercentage, Double amount) {
            return interestPercentage/100 * amount;
    }


}
