package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotBlank
    @Email
    @Schema(description = "Nombre de usuario", example = "juan.perez@example.com", required = true)
    String userName;

    @NotBlank
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    String lastName;
}
