package AlkemyWallet.AlkemyWallet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column(nullable = false)
    @NotNull
    @Email
    private String email;

    @Column(nullable = false)
    @NotNull
    private String password;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime creationDate;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime updateDate;

    @Column(nullable = false)
    @NotNull
    private boolean softDelete;

   // @ManyToOne
   // @JoinColumn(name='role_id', nullable = false)
    //private Role roleid;

}
