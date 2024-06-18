package AlkemyWallet.AlkemyWallet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Period;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

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
    @NotBlank
    @Email
    private String userName;

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
    private Integer softDelete;

    @Column(nullable = false)
    @NotNull
    private String dni;

    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete ? 1 : 0; // Almacena 1 si es true, 0 si es false
    }

    public boolean isSoftDelete() {
        return this.softDelete == 1; // Devuelve true si softDelete es 1
    }

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = true)
    private String imagePath;

    @ElementCollection
    private List<String> cbuTerceros;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears();
    }

    @Override
    public boolean isAccountNonExpired() {

        return this.softDelete == 0;
    }

    @Override
    public boolean isAccountNonLocked() {

        return this.softDelete == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.softDelete == 0;
    }

    @Override
    public boolean isEnabled() {

        return this.softDelete == 0;
    }


     @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="role_id", nullable = false, referencedColumnName = "id")
    private Role role;

}

