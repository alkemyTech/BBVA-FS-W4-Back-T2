package AlkemyWallet.AlkemyWallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDTO {

    private Long id;

    private String userName;

    private String firstName;

    private String lastName;

    private String imagePath;


}
