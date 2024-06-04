package AlkemyWallet.AlkemyWallet.exceptions;


public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String message) {
        super(message);
    }
}