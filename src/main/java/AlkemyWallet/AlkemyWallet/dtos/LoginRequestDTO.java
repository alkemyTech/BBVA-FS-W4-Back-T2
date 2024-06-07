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
public class LoginRequestDTO {

        @NotBlank
        @Email
        @NotNull
        @Schema(description = "Nombre de usuario (correo electrónico)", example = "juan.perez@example.com", required = true)
        String userName;
        @NotBlank
        @NotNull
        @Schema(description = "Contraseña del usuario", example = "password123", required = true)
        String password;
 }