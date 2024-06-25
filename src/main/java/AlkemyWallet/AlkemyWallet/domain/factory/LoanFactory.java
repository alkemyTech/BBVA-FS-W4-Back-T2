package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.dtos.LoanResponseDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class LoanFactory {


    public LoanResponseDTO createLoanSimulation(Double amount, Integer months, Double monthlyAmount, Double totalAmount, Double interestPercentage) {
        return new LoanResponseDTO(amount, totalAmount, months, monthlyAmount,interestPercentage);

    };
}
