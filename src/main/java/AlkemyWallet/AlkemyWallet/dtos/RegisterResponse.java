package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    @NotBlank
    @Email
    @Schema(description = "Nombre de usuario", example = "juan.perez@example.com", required = true)
    String userName;

    @NotBlank
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    String lastName;

    @Schema(description = "Id del usuario", example = "1", required = true)
    Long id;

    @Schema(description = "Imagen del usuario", required = true)
    String imagePath;

    @Schema(description = "Fecha de nacimiento del usuario", example = "2003-03-10", required = true)
    String birthDay;
}
