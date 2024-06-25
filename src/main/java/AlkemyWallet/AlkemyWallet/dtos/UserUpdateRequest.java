package AlkemyWallet.AlkemyWallet.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "El primer nombre es obligatorio")
    private String firstName;
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
