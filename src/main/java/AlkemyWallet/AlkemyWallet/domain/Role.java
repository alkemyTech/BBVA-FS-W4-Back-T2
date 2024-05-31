package AlkemyWallet.AlkemyWallet.domain;

import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name="roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum name;
    @Column(name="description")
    private String description;
    @Column(name="creationDate")
    private LocalDateTime creationDate;
    @Column(name="updateDate")
    private LocalDateTime updateDate;


}
