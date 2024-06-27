package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetailDTO {

    @Schema(description = "Nombre del usuario", required = true)
    @NotNull
    @NotBlank
    private String firstName;

    @Schema(description = "Apellido del usuario", required = true)
    @NotNull
    @NotBlank
    private String lastName;

    @Schema(description = "Nombre de usuario", required = true)
    @NotNull
    @NotBlank
    private String username;

    @Schema(description = "Rol del usuario", required = true)
    @NotNull
    private RoleEnum roleName;

    @Schema(description = "Fecha de nacimiento del usuario", required = true, format = "date")
    @NotNull
    @NotBlank
    private LocalDate birthDate;

    @Schema(description = "Ruta de la imagen del usuario")
    private String imagePath;

    @Schema(description = "DNI del Usuario", required = true)
    @NotNull
    @NotBlank
    private String dni;
}
