package AlkemyWallet.AlkemyWallet.dtos;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Email(message = "Please provide a valid email address")
    private String userName;

    @NotBlank
    private String password;

    @NotNull(message = "First name must not be null")
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String birthDate;
}