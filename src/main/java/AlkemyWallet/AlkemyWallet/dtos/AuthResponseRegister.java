package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseRegister {
    @NotNull
    String token;
    @NotBlank
    @Email
    String userName;
    @NotBlank
    String firstName;
    String lastName;
}
