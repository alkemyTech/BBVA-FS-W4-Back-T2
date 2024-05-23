package AlkemyWallet.AlkemyWallet.domain;

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
    long id;

    @NotNull
    @NotBlank
    @Column(name = "currency", nullable = false)
    String currency;

    @NotNull
    @Column(name = "transactionLimit", nullable = false)
    Double transactionLimit;

    @NotNull
    @Column(name = "balance", nullable = false)
    Double balance;

    @NotNull
    @Column(name = "creationDate", nullable = false)
    LocalDateTime creationDate;

    @NotNull
    @Column(name = "updateDate" , nullable = false)
    LocalDateTime updateDate;

    @NotNull
    @Column(name = "softDelete", nullable = false)
    Boolean softDelete;

    @NotNull
    @NotBlank
    @Column(name = "CBU", nullable = false)
    String CBU;


    @ManyToOne()
    @JoinColumn(name="user_id", nullable = false)
    long userId;
}
