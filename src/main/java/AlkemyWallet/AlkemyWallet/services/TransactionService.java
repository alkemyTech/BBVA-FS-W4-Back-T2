package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.TransactionFilter;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.mappers.TransactionResponseMapper;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.dtos.PaymentResponseDTO;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionFactory transactionFactory;
    private final UserService userService;
    private final TransactionResponseMapper transactionResponseMapper;
    private final PaginationConfig paginationConfig;

    public TransactionResponse  registrarTransaccion(TransactionDTO transaction, Accounts originAccount) {
        Double amount = transaction.getAmount();
        Accounts destinationAccount = accountService.findByCBU(transaction.getDestino());

        CurrencyEnum transactionCurrency;
        try {
            transactionCurrency = CurrencyEnum.valueOf(transaction.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + transaction.getCurrency());
        }

        if (!transactionCurrency.equals(originAccount.getCurrency())) {
            throw new IncorrectCurrencyException("La moneda seleccionada no es la correcta para este tipo de cuenta");
        }

        if (!originAccount.dineroDisponible(amount) || !originAccount.limiteDisponible(amount)) {
            throw new InsufficientFundsException("No hay suficiente dinero o límite disponible para completar la transacción");
        }

        Transaction transactionRegistro = this.sendMoney(transaction, originAccount, destinationAccount);
        this.receiveMoney(transaction, originAccount, destinationAccount);
        accountService.updateAfterTransaction(originAccount, amount);
        accountService.updateAfterTransaction(destinationAccount, -amount);

        return transactionResponseMapper.mapToTransactionResponse(transactionRegistro, originAccount, destinationAccount);
    }

    public Transaction sendMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction paymentTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.PAYMENT,
                "",
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );

        if (paymentTransaction == null) {
            throw new RuntimeException("Failed to create transaction");
        }

        transactionRepository.save(paymentTransaction);
        return paymentTransaction;
    }



    public void receiveMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction incomeTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.INCOME,
                "",
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );
        transactionRepository.save(incomeTransaction);
    }


    public Long depositMoney(TransactionDTO transaction, Accounts account) {
        try {
            Accounts destinationAccount = accountService.findByCBU(transaction.getDestino());
            validateDepositTransaction(transaction, account, destinationAccount);

            // Crear una transacción de depósito para la cuenta de origen
            Transaction depositTransaction = transactionFactory.createTransaction(
                    transaction.getAmount(),
                    TransactionEnum.DEPOSIT,
                    "",
                    LocalDateTime.now(),
                    account,
                    account // La cuenta de origen es la misma que la cuenta de destino en un depósito
            );
            transactionRepository.save(depositTransaction);

            // Actualizar el saldo de la cuenta de origen
            accountService.updateAfterTransaction(account, -transaction.getAmount());

            return depositTransaction.getId();
        } catch (NonPositiveAmountException e) {
            throw e;
        } catch (UnauthorizedTransactionException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Se produjo un error inesperado al procesar el depósito: " + e.getMessage());
            throw new RuntimeException("Error al procesar el depósito");
        }
    }

    private void validateDepositTransaction(TransactionDTO transaction, Accounts account, Accounts destinationAccount) {
        if (transaction.getAmount() <= 0) {
            throw new NonPositiveAmountException("El monto del depósito debe ser mayor que cero");
        }
        if (!Objects.equals(account.getCBU(), destinationAccount.getCBU())) {
            throw new UnauthorizedTransactionException("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino");
        }
    }

    public List<Transaction> getTransactionsByAccount(Accounts account) {
        try {
            return transactionRepository.findByAccount(account);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        try {
            Accounts account = accountService.findById(accountId); // Obtener la cuenta completa
            return getTransactionsByAccount(account);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }

    public Page<Transaction> getTransactionsByUserIdPaginated(Long userId, int page) {
        int transactionsPerPage = paginationConfig.getTransactionsPerPage();
        Pageable pageable = PageRequest.of(page,transactionsPerPage);
        User user = userService.findById(userId).get();
        List<Accounts> cuentasDelUsuario = accountService.findAccountsByUserId(user.getId());
        List<Transaction> allTransactions = new ArrayList<>();

        for (Accounts account : cuentasDelUsuario) {
            Page<Transaction> transactionsPage = transactionRepository.findByOriginAccountOrAccount(account.getId(), pageable);
            allTransactions.addAll(transactionsPage.getContent());
        }

        int totalElements = allTransactions.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);

        return new PageImpl<>(allTransactions.subList(start, end), pageable, totalElements);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }

    public Transaction updateTransactionDescription(Long id, String description, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

        // Verificar que la transacción pertenece al usuario autenticado
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new SecurityException("No está autorizado para actualizar esta transacción");
        }

        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    public PaymentResponseDTO registrarPago(TransactionDTO transaction, Accounts originAccount) {
        Double amount = transaction.getAmount();

        CurrencyEnum transactionCurrency;
        try {
            transactionCurrency = CurrencyEnum.valueOf(transaction.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + transaction.getCurrency());
        }

        if (!transactionCurrency.equals(originAccount.getCurrency())) {
            throw new IncorrectCurrencyException("La moneda seleccionada no es la correcta para este tipo de cuenta");
        }

        if (!originAccount.dineroDisponible(amount) || !originAccount.limiteDisponible(amount)) {
            throw new InsufficientFundsException("No hay suficiente dinero o límite disponible para completar la transacción");
        }

        Transaction transactionRegistro = this.sendPayment(transaction, originAccount, transaction.getDestino());
        accountService.updateAfterTransaction(originAccount, amount);

        return transactionResponseMapper.mapToPaymentResponse(transactionRegistro, originAccount);
    }

    public Transaction sendPayment(TransactionDTO transaction, Accounts originAccount, String destino) {
        Transaction paymentTransaction = transactionFactory.createTransactionPayment(
                transaction.getAmount(),
                transaction.getDescription(),
                LocalDateTime.now(),
                originAccount,
                destino
        );

        if (paymentTransaction == null) {
            throw new RuntimeException("Failed to create transaction");
        }

        transactionRepository.save(paymentTransaction);
        return paymentTransaction;
    }

    public List<Transaction> getLast10TransactionsByAccountId(Long accountId) {
        try {
            Accounts account = accountService.findById(accountId); // Obtener la cuenta completa
            return getTransactionsByAccount(account).stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed()) // Ordenar por fecha de forma descendente
                    .limit(10) // Limitar a las últimas 10 transacciones
                    .collect(Collectors.toList()); // Convertir a lista
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }

    public List<TransactionResponse> mapTransactionsToResponses(List<Transaction> transactions) {
        List<TransactionResponse> responseList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponse response = new TransactionResponse();
            response.setDestino(transaction.getAccount().getCBU());
            response.setOrigen(transaction.getOriginAccount() != null ? transaction.getOriginAccount().getCBU() : null);
            response.setFechaDeTransaccion(transaction.getTransactionDate().toLocalDate());
            response.setTipoDeTransaccion(transaction.getType());
            response.setCurrency(String.valueOf(transaction.getOriginAccount().getCurrency()));

            responseList.add(response);
        }

        return responseList;
    }

    public Page<TransactionResponse> getTransactionsWithFilters(TransactionFilter filter) {
        try {
            // Obtener las cuentas del usuario por su userId
            List<Accounts> userAccounts = accountService.findAccountsByUserId(filter.getUserId());

            // Obtener las transacciones filtradas por las cuentas del usuario
            Pageable pageable = PageRequest.of(filter.getPage(), 10, Sort.by("transactionDate").descending());

            Specification<Transaction> spec = Specification.where(accountIn(userAccounts))
                    .and(transactionDateBetween(filter.getFromDate(), filter.getToDate()))
                    .and(transactionTypeEquals(filter.getTransactionType()));

            Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

            List<TransactionResponse> responseList = mapTransactionsToResponses(transactionPage.getContent());

            return new PageImpl<>(responseList, pageable, transactionPage.getTotalElements());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener transacciones con filtros", e);
        }
    }

    // Método auxiliar para construir Specification basado en las cuentas del usuario
    private Specification<Transaction> accountIn(List<Accounts> userAccounts) {
        return (root, query, criteriaBuilder) -> root.get("account").in(userAccounts);
    }


    private Specification<Transaction> userIdEquals(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    private Specification<Transaction> transactionDateBetween(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("fecha"), fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
        } else if (fromDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fromDate.atStartOfDay());
        } else if (toDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), toDate.atTime(LocalTime.MAX));
        }
        return null;
    }

    private Specification<Transaction> transactionTypeEquals(String transactionType) {
        if (transactionType != null && !transactionType.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tipo"), transactionType);
        }
        return null;
    }
}