package AlkemyWallet.AlkemyWallet.domain;


import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transaction")
@AllArgsConstructor
@NoArgsConstructor

public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
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
    private Accounts accountId;
}
