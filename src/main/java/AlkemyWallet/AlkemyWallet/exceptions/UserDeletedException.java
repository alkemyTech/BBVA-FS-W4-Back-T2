package AlkemyWallet.AlkemyWallet.exceptions;

import javax.naming.AuthenticationException;

public class UserDeletedException extends AuthenticationException {
    public UserDeletedException(String message) {
        super(message);
    }
}
