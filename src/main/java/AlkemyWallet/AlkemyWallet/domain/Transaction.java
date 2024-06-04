package AlkemyWallet.AlkemyWallet.domain;


import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transactions")
@AllArgsConstructor
@NoArgsConstructor

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotNull
    private double amount;
    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionEnum type;
    @Column
    @Nullable
    private String description;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime transactionDate;
    @ManyToOne
    @JoinColumn(name = "Account_Id", nullable = false)
    @NotNull
    private Accounts accountId; // account destino

    @ManyToOne
    @JoinColumn(name = "origin_account_id", nullable = true)
    private Accounts originAccount;  // Origin account


    public Transaction(Double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Accounts accountId, Accounts originAccount) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
        this.originAccount = originAccount;

    }

    public Accounts getAccount() {
        return this.accountId;
    }
}



