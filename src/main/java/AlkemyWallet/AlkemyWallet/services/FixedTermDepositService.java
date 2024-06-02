package AlkemyWallet.AlkemyWallet.services;


import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;



@Service
@AllArgsConstructor
public class FixedTermDepositService {

    FixedTermDepositConfig config;

    public FixedTermDepositDto simulateFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto){
        //Lógica del plazo Fijo

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate fechaInicial = LocalDate.parse(fixedTermDepositDto.getCreationDate(),formatter);
        LocalDate fechaFinal = LocalDate.parse(fixedTermDepositDto.getClosingDate(),formatter);

        LocalDateTime fechaYHoraInicial = LocalDateTime.of(fechaInicial, LocalDateTime.now().toLocalTime());
        LocalDateTime fechaYHoraFinal = LocalDateTime.of(fechaFinal, LocalDateTime.now().toLocalTime());


        long horasDiferencia = ChronoUnit.HOURS.between(fechaYHoraInicial,fechaYHoraFinal);
        int diasDelPlazo = (int) (horasDiferencia / 24);

        Double interest = config.getFixedTermInterest();
        Double invertedAmount = fixedTermDepositDto.getInvertedAmount();
        Double interesGanado = ((invertedAmount*interest)/100)*diasDelPlazo;
        Double totalAmountToCollect = interesGanado+invertedAmount;

        fixedTermDepositDto.setGainedInterest(interesGanado);
        fixedTermDepositDto.setTotalAmountToCollect(totalAmountToCollect);

        return fixedTermDepositDto;
    }
}
