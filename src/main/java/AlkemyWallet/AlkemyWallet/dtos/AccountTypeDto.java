//Lo dejo por si a alguien le sirve
package AlkemyWallet.AlkemyWallet.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountTypeDto {
    @NotBlank
    @NotNull
    @Schema(description = "Tipo de cuenta", example = "CORRIENTE", required = true)
    private String accountType;
}
