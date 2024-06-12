package AlkemyWallet.AlkemyWallet.domain;


import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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

    @Getter
    @ManyToOne
    @JoinColumn(name = "Account_Id", nullable = true)
    private Accounts account; // account destino

    @ManyToOne
    @JoinColumn(name = "origin_account_id", nullable = true)
    private Accounts originAccount;  // Origin account

    @Column
    private String destinoExterno;


    public Transaction(Double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Accounts accountId, Accounts originAccount) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.account = accountId;
        this.originAccount = originAccount;

    }

    public Transaction(Double amount, TransactionEnum type,String description, LocalDateTime transactionDate, Accounts originAccount, String destinoExterno) {
        this.amount = amount;
        this.description = description;
        this.type=type;
        this.transactionDate = transactionDate;
        this.destinoExterno= destinoExterno;
        this.originAccount = originAccount;
    }

}


