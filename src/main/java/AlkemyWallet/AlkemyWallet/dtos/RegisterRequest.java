package AlkemyWallet.AlkemyWallet.dtos;



import io.swagger.v3.oas.annotations.media.Schema;
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
    @Email(message = "Por favor proporcione una dirección de correo válida")
    @Schema(description = "Nombre de usuario (correo electrónico)", example = "juan.perez@example.com", required = true)
    private String userName;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;

    @NotNull(message = "El nombre no debe ser nulo")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @NotBlank
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @NotBlank
    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01", required = true)
    private String birthDate;

    @NotBlank
    @NotNull
    @Schema(description = "DNI del usuario", example = "12345678", required = true)
    private Integer dni;
}
