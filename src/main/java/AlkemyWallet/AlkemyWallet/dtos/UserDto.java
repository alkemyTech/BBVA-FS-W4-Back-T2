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
public class UserDto {

    @Schema(description = "Nombre del usuario", required = true)
    @NotNull
    @NotBlank
    private String firstName;

    @Schema(description = "Apellido del usuario", required = true)
    @NotNull
    @NotBlank
    private String lastName;

    @Schema(description = "Correo electrónico del usuario", required = true)
    @NotNull
    @NotBlank
    @Email
    private String userName;

    @Schema(description = "DNI del Usuario", required = true)
    @NotNull
    @NotBlank
    private String dni;
}
