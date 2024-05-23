package AlkemyWallet.AlkemyWallet.domain;

import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name="Roles")
public class Role {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleEnum name;
    @Column(name="description")
    private String description;
    @Column(name="creationDate")
    private LocalDateTime creationDate;
    @Column(name="updateDate")
    private LocalDateTime updateDate;

}
