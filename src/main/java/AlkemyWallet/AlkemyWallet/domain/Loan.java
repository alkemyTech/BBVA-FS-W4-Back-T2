package AlkemyWallet.AlkemyWallet.domain;

import AlkemyWallet.AlkemyWallet.config.LoanConfig;
import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Entity
@Table(name="loans")
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false)
    private Double originalAmount;
    @NotNull
    @Column(nullable = false)
    private Double totalAmount;
    @NotNull
    @Column(nullable = false)
    private Integer months;
    @NotNull
    @Column(nullable = false)
    private Double monthlyAmount;
    @NotNull
    @Column(nullable = false)
    private Double interestPercentage;


}
