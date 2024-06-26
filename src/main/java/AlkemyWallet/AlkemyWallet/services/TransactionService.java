package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.TransactionFilter;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.mappers.TransactionResponseMapper;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.dtos.PaymentResponseDTO;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    private final AccountRepository accountRepository;

    //EndPoint Envio de Dinero

    public void checkDestinationCurrency(String accountCBU, CurrencyEnum currency){
        Accounts account = accountService.findByCBU(accountCBU);
        if (!(account.getCurrency()==currency)){
            throw new IncorrectCurrencyException("La moneda seleccionada no es la correcta para este tipo de cuenta");
        }
    }

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
        accountService.updateAfterTransaction(originAccount, -amount);
        accountService.updateAccountBalance(destinationAccount, amount);

        return transactionResponseMapper.mapToTransactionResponse(transactionRegistro, originAccount, destinationAccount);
    }

    public Transaction sendMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction paymentTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.PAYMENT,
                transaction.getDescription(),
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
                transaction.getDescription(),
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );
        transactionRepository.save(incomeTransaction);
    }

    //Endpoint deposito de dinero/carga de saldo
    public Long depositMoney(TransactionDTO transaction, Accounts account) {
        try {
            Accounts destinationAccount=accountService.findById(Long.valueOf(transaction.getDestino()));
           //Accounts destinationAccount = accountService.findByCBU(transaction.getDestino());
            validateDepositTransaction(transaction, account, destinationAccount);

            // Crear una transacción de depósito para la cuenta de origen
            Transaction depositTransaction = transactionFactory.createTransaction(
                    transaction.getAmount(),
                    TransactionEnum.DEPOSIT,
                    transaction.getDescription(),
                    LocalDateTime.now(),
                    account,
                    account // La cuenta de origen es la misma que la cuenta de destino en un depósito
            );
            transactionRepository.save(depositTransaction);

            // Actualizar el saldo de la cuenta de origen
            accountService.updateAccountBalance(account, transaction.getAmount());

            return depositTransaction.getId();
        } catch (NonPositiveAmountException | UnauthorizedTransactionException e) {
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

    //Endpoint Payment
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
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("El monto no puede ser nulo o negativo");
        }

        // Verificar si el destino es una cuenta interna en la base de datos
        Accounts destinationAccount = null;
        try {
            destinationAccount = accountService.findByCBU(transaction.getDestino());
        } catch (RuntimeException ignored) {
        }

        if (destinationAccount != null) {

            Transaction paymentTransaction = sendMoney(transaction, originAccount, destinationAccount);
            accountService.updateAfterTransaction(originAccount, -amount);
            accountService.updateAccountBalance(destinationAccount, amount);
            this.receiveMoney(transaction, originAccount, destinationAccount);
            return transactionResponseMapper.mapToPaymentResponse(paymentTransaction, originAccount, destinationAccount.getCBU());

        } else {

            Transaction paymentTransaction = sendExternPayment(transaction, originAccount, transaction.getDestino());
            accountService.updateAfterTransaction(originAccount, -amount);
            return transactionResponseMapper.mapToPaymentResponse(paymentTransaction, originAccount, transaction.getDestino());
        }
    }

    public Transaction sendExternPayment(TransactionDTO transaction, Accounts originAccount, String destino) {
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


    public List<Transaction> getTransactionsByAccount(Accounts account) {
        try {
            List<Transaction> transactions = transactionRepository.findDepositsByAccountId(account.getId());
            transactions.addAll(transactionRepository.findIncomesByAccountId(account.getId()));
            transactions.addAll(transactionRepository.findPaymentsByAccountId(account.getId()));
            return transactions;
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }



    public List<TransactionResponse> mapTransactionsToResponses(List<Transaction> transactions) {
        List<TransactionResponse> responseList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponse response = new TransactionResponse();
            response.setDestino(transaction.getAccount() != null ? transaction.getAccount().getCBU() : null);
            response.setOrigen(transaction.getOriginAccount() != null ? transaction.getOriginAccount().getCBU() : null);
            response.setFechaDeTransaccion(transaction.getTransactionDate().toLocalDate());
            response.setTipoDeTransaccion(transaction.getType());
            response.setCurrency(transaction.getOriginAccount().getCurrency().toString());
            response.setDescripcion(transaction.getDescription());
            response.setAmount(transaction.getAmount());
            response.setNombreDestino(accountService.obtenerUserAccountName(transaction.getAccount()));

            responseList.add(response);
        }

        return responseList;
    }

    public Page<TransactionResponse> getTransactionsWithFilters(TransactionFilter filter) {
        try {
            List<Accounts> userAccounts = accountService.findAccountsByUserId(filter.getUserId());
            Pageable pageable = PageRequest.of(filter.getPage(), 10, Sort.by("transactionDate").descending());

            Specification<Transaction> spec = Specification.where(accountIn(userAccounts));

            if (filter.getFromDate() != null || filter.getToDate() != null) {
                spec = spec.and(transactionDateBetween(filter.getFromDate(), filter.getToDate()));
            }

            if (filter.getTransactionType() != null && !filter.getTransactionType().isEmpty()) {
                spec = spec.and(transactionTypeEquals(filter.getTransactionType()));
            }

            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                spec = spec.and(accountCurrencyEquals(filter.getCurrency()));
            }

            Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
            List<TransactionResponse> responseList = mapTransactionsToResponses(transactionPage.getContent());

            return new PageImpl<>(responseList, pageable, transactionPage.getTotalElements());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener transacciones con filtros", e);
        }
    }

    // Método auxiliar para construir Specification basado en las cuentas del usuario
    public static Specification<Transaction> accountIn(List<Accounts> userAccounts) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate depositOrPaymentPredicate = criteriaBuilder.and(
                    root.get("originAccount").in(userAccounts),
                    root.get("type").in("DEPOSIT", "PAYMENT") // Adjusted to "type"
            );

            Predicate incomePredicate = criteriaBuilder.and(
                    root.get("account").in(userAccounts),
                    criteriaBuilder.equal(root.get("type"), "INCOME") // Adjusted to "type"
            );

            return criteriaBuilder.or(depositOrPaymentPredicate, incomePredicate);
        };
    }

    private Specification<Transaction> transactionDateBetween(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("transactionDate"), fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
        } else if (fromDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), fromDate.atStartOfDay());
        } else if (toDate != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), toDate.atTime(LocalTime.MAX));
        }
        return null;
    }

    private Specification<Transaction> transactionTypeEquals(String transactionType) {
        if (transactionType != null && !transactionType.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), transactionType);
        }
        return null;
    }

    private Specification<Transaction> accountCurrencyEquals(String currency) {
        if (currency != null && !currency.isEmpty()) {
            return (root, query, criteriaBuilder) -> {
                Join<Transaction, Accounts> originAccountJoin = root.join("originAccount", JoinType.LEFT);
                return criteriaBuilder.equal(originAccountJoin.get("currency"), currency);
            };
        }
        return null;
    }

}