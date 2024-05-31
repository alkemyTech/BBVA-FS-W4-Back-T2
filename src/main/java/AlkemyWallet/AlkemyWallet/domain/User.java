package AlkemyWallet.AlkemyWallet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !softDelete;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !softDelete;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !softDelete;
    }

    @Override
    public boolean isEnabled() {
        return !softDelete;
    }

     @ManyToOne
    @JoinColumn(name="role_id", nullable = false, referencedColumnName = "id")
    private Role role;

}
