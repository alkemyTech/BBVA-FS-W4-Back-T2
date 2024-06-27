package AlkemyWallet.AlkemyWallet.mappers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.UserDetailDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDetailMapper {
    public UserDetailDTO toUserDetailDto(User user){
        return new UserDetailDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole().getName(),
                user.getBirthDate(),
                user.getImagePath(),
                user.getDni()
                );
    }
}

