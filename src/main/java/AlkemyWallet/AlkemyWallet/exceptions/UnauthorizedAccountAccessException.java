package AlkemyWallet.AlkemyWallet.exceptions;

public class UnauthorizedAccountAccessException extends RuntimeException {
    public UnauthorizedAccountAccessException(String message) {
        super(message);
    }
}

