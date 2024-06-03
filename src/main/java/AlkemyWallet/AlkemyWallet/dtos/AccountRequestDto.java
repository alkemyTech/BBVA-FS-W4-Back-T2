package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {
    @Valid
    private String currency;

    @Valid
    private String accountType;
}
