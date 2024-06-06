package AlkemyWallet.AlkemyWallet.handlers;

import AlkemyWallet.AlkemyWallet.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String readableMessage = makeErrorMessageReadable(e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(readableMessage);
    }

    private String makeErrorMessageReadable(String errorMessage) {
        if (errorMessage.contains("not-null property references a null or transient value")) {
            String[] parts = errorMessage.split(" : ");
            if (parts.length > 1) {
                return "The field '" + parts[1].split("\\.")[parts[1].split("\\.").length - 1] + "' cannot be empty.";
            }
        }
        return "An error occurred. Please check your data and try again.";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    //== EXCPCIONES DE TRANSACCIONES ==//
    @ExceptionHandler(NonPositiveAmountException.class)
    public ResponseEntity<String> handleNonPositiveAmountException(NonPositiveAmountException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(IncorrectCurrencyException.class)
    public ResponseEntity<String> handleIncorrectCurrencyException(IncorrectCurrencyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


    //==EXCEPCIONES DE FIXED DEPOSIT==//
    @ExceptionHandler(MinimumDurationException.class)
    public ResponseEntity<String> handleMinimumDurationException(MinimumDurationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidDateOrderException.class)
    public ResponseEntity<String> handleInvalidDateOrderException(InvalidDateOrderException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    //==EXCEPCIONES ACCOUNT==//
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<String> handleDuplicateAccountException(DuplicateAccountException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    //==EXCPECIONES USER ==//
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }



}