package AlkemyWallet.AlkemyWallet.dtos;


import lombok.Data;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String password;
}
