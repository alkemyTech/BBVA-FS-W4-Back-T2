package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.BalanceDTO;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositBalanceDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionBalanceDTO;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BalanceService {

        @Autowired
        private AccountService accountService;

        @Autowired
        private FixedTermDepositService fixedTermDepositService;

        @Autowired TransactionService transactionService;

        public BalanceDTO getUserBalanceAndTransactions(Long userId) {
            // Obtener todas las cuentas del usuario
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);

            List<TransactionBalanceDTO> transactionsDtos = new ArrayList<>();
            List<FixedTermDepositBalanceDTO> fixedTermDepositBalanceDTOS = new ArrayList<>();

            // Calcular el balance total en ARS y USD
            Double totalArsBalance = 0.0;
            Double totalUsdBalance = 0.0;

            for (Accounts account : accounts) {
                Long accountId = account.getId();

                // Calcular el balance total
                if (account.getCurrency().equals(CurrencyEnum.ARS)) {
                    totalArsBalance += accountService.getBalanceInARS(account);
                } else if (account.getCurrency().equals(CurrencyEnum.USD)) {
                    totalUsdBalance += accountService.getBalanceInUSD(account);
                }

                List<Transaction> accountTransactions = transactionService.getTransactionsByAccountId(accountId);

                for (Transaction transaction : accountTransactions) {
                    TransactionBalanceDTO dto = new TransactionBalanceDTO();
                    dto.setId(transaction.getId());
                    dto.setAmount(transaction.getAmount());
                    dto.setTransactionDate(transaction.getTransactionDate());
                    dto.setDescription(transaction.getDescription());
                    dto.setType(transaction.getType());
                    dto.setCurrency(transaction.getOriginAccount().getCurrency().toString());
                    dto.setOriginAccountCBU(transaction.getOriginAccount().getCBU());
                    transactionsDtos.add(dto);
                }
            }

            // Obtener los plazos fijos del usuario
            List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositService.getFixedTermDepositsByUser(userId);
            for (FixedTermDeposit deposit : fixedTermDeposits) {
                FixedTermDepositBalanceDTO dto = new FixedTermDepositBalanceDTO();
                dto.setAmount(deposit.getAmount());
                dto.setCreationDate(deposit.getCreationDate().toString());
                dto.setClosingDate(deposit.getClosingDate().toString());
                fixedTermDepositBalanceDTOS.add(dto);
            }

            // Crear DTO de respuesta
            BalanceDTO balanceDTO = new BalanceDTO();
            balanceDTO.setAccountArs(totalArsBalance);
            balanceDTO.setAccountUsd(totalUsdBalance);
            balanceDTO.setFixedTerms(fixedTermDepositBalanceDTOS);
            balanceDTO.setAccountTransactions(transactionsDtos);
            return balanceDTO;
        }


}
