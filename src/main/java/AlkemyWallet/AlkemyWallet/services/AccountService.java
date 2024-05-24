package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    public final ModelMapper modelMapper;

    public AccountService(ModelMapper modelMapper, AccountRepository accountRepository) {
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
    }

    public Accounts add(Accounts accounts){
        accounts = modelMapper.map(accounts,Accounts.class);
        accounts.setCreationDate(LocalDateTime.now());
        return accountRepository.save(accounts);
    }




}
