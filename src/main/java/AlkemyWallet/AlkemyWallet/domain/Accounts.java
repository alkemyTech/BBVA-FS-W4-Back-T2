package AlkemyWallet.AlkemyWallet.domain;

import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="accounts")
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyEnum currency;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "accountType", nullable = false)
    private AccountTypeEnum accountType;

    @NotNull
    @Column(name = "transactionLimit", nullable = false)
    private Double transactionLimit;

    @NotNull
    @Column(name = "balance", nullable = false)
    private Double balance;

    @NotNull
    @Column(name = "creationDate", nullable = false)
    private LocalDateTime creationDate;

    @NotNull
    @Column(name = "updateDate" , nullable = false)
    private LocalDateTime updateDate;

    @NotNull
    @Column(name = "softDelete", nullable = false)
    private Boolean softDelete;

    @NotNull
    @NotBlank
    @Column(name = "CBU", nullable = false)
    @Size(min=22, max=22)
    private String CBU;


    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User userId;

    public boolean dineroDisponible(Double montoATransferir) {
        return balance - montoATransferir >= 0;
    }

    public boolean limiteDisponible(Double montoATransferir) {
        return transactionLimit - montoATransferir >= 0;
    }

    public void updateBalance(Double amount) {
        this.setBalance(balance - amount);
    }

    public void updateLimit(Double amount) {
        this.setTransactionLimit(transactionLimit - amount);
    }

    //Probar si de esta forma cuando inicio una nueva cuenta
    //la fecha de creacion de esta se crea automaticamente
    @PrePersist
    protected void onCreate(){
        //Estará bien "this.???"
        this.balance = 0.00;
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.softDelete = false;
    }


}