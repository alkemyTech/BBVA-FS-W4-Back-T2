package AlkemyWallet.AlkemyWallet.domain;


import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transactions")
@AllArgsConstructor
@NoArgsConstructor

public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column (nullable = false)
    @NotNull
    private double amount;
    @Column (nullable = false)
    @NotNull
    @Enumerated (EnumType.STRING)
    private TransactionEnum type;
    @Column
    @Nullable
    private String description;
    @Column (nullable = false)
    @NotNull
    private LocalDateTime transactionDate;
    @ManyToOne
   @JoinColumn (name="Account_Id", nullable = false)
    @NotNull
    private Accounts accountId;


    public Transaction(Double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Accounts accountId) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
    }
}
