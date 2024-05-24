package AlkemyWallet.AlkemyWallet.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fixed_term_deposits")
public class FixedTermDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    @NotNull
    private Double interest;
    @Column
    private LocalDateTime creationDate;
    @Column
    private LocalDateTime closingDate;
}
