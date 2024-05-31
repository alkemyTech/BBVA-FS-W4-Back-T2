package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;
import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;



@Service
@AllArgsConstructor
public class FixedTermDepositService {

    FixedTermDepositConfig config;

    public FixedTermDepositDto simulateFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto){
        //Lógica del plazo Fijo

        LocalDateTime inicio = fixedTermDepositDto.getCreationDate();
        LocalDateTime fin = fixedTermDepositDto.getClosingDate();

        long horasDiferencia = ChronoUnit.HOURS.between(inicio,fin);
        int diasDelPlazo = (int) (horasDiferencia / 24);
        //Capaz lo llegue a necesitar
//        if (horasDiferencia % 24 != 0) {
//            diasDelPlazo++;
//        }
        Double interest = config.getFixedTermInterest();
        Double invertedAmount = fixedTermDepositDto.getInvertedAmount();
        Double interesGanado = ((invertedAmount*interest)/100)*diasDelPlazo;
        Double totalAmountToCollect = interesGanado+invertedAmount;

        fixedTermDepositDto.setGainedInterest(interesGanado);
        fixedTermDepositDto.setTotalAmountToCollect(totalAmountToCollect);

        return fixedTermDepositDto;
    }
}
