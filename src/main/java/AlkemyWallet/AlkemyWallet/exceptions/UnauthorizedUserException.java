package AlkemyWallet.AlkemyWallet.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedUserException extends AuthenticationException {
    public UnauthorizedUserException(String message) { super(message); }
}
