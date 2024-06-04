package AlkemyWallet.AlkemyWallet.exceptions;

public class UnauthorizedTransactionException extends RuntimeException {
    public UnauthorizedTransactionException(String message) { super(message); }
}
