package AlkemyWallet.AlkemyWallet.dtos;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetailDTO {
    @NotNull
    @NotBlank
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private RoleEnum roleName;

    @NotNull
    @NotBlank
    private LocalDate birthDate;

    private String imagePath;
}
