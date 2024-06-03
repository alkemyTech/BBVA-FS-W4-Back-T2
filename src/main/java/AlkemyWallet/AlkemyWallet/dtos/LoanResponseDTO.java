package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LoanResponseDTO {


    @NotNull
    private Double originalAmount;

    @NotNull
    private Double totalAmount;

    @NotNull
    private Integer months;

    @NotNull
    private Double monthlyAmount;

    @NotNull
    private Double interestPercentage;


}