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
    @Column(name = "currency")
    String currency;

    @NotNull
    @Column(name = "transactionLimit")
    Double transactionLimit;

    @NotNull
    @Column(name = "balance")
    Double balance;

    @NotNull
    @Column(name = "creationDate")
    LocalDateTime creationDate;

    @NotNull
    @Column(name = "updateDate")
    LocalDateTime updateDate;

    @NotNull
    @Column(name = "softDelete")
    Boolean softDelete;

    @NotNull
    @NotBlank
    @Column(name = "CBU")
    String CBU;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne()
    @JoinColumn(name="user_id", nullable = false)
    long userId;
}
