package AlkemyWallet.AlkemyWallet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    private boolean softDelete;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
        return !softDelete;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
        return !softDelete;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
        return !softDelete;
    }

    @Override
    public boolean isEnabled() {
        return true;
        return !softDelete;
    }

     @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="role_id", nullable = false, referencedColumnName = "id")
    private Role role;

}
