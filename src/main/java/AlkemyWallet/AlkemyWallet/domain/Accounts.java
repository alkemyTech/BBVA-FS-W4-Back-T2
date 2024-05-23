package AlkemyWallet.AlkemyWallet.domain;

import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyEnum currency;

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
    private String CBU;


    @ManyToOne()
    @JoinColumn(name="user_id", nullable = false)
    long userId;
}