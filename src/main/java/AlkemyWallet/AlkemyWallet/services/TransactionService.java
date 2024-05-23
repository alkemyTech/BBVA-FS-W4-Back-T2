package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;


}
