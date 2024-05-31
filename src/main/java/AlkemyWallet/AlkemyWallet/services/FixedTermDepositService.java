package AlkemyWallet.AlkemyWallet.services;

// Imports omitidos para mayor claridad

import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.FixedTermDepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FixedTermDepositService {
    private final FixedTermDepositRepository fixedTermDepositRepository;
    private final UserService userService; // Añadir UserService para obtener la entidad User desde el ID

    public List<FixedTermDeposit> getFixedTermDepositsByUser(Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return fixedTermDepositRepository.findByUser(user);
    }

    // Otros métodos omitidos para mayor claridad
}

