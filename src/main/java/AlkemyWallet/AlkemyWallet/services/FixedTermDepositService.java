package AlkemyWallet.AlkemyWallet.services;

// Imports omitidos para mayor claridad

import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;
import AlkemyWallet.AlkemyWallet.controllers.FixedTermDepositController;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;


import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.repositories.FixedTermDepositRepository;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;



@Service
@AllArgsConstructor
public class FixedTermDepositService {

    private final UserService userService;
    FixedTermDepositConfig config;
    AccountService accountService;
    JwtService jwtService;
    FixedTermDepositRepository fixedTermDepositRepository;

    public FixedTermDepositDto simulateFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate fechaInicial = LocalDate.parse(fixedTermDepositDto.getCreationDate(),formatter);
        LocalDate fechaFinal = LocalDate.parse(fixedTermDepositDto.getClosingDate(),formatter);

        try {

            // Verificamos si la fecha final es mayor que la fecha inicial
            if (fechaFinal.isBefore(fechaInicial)) {
                throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial");
            }

            // Calculamos la duración en días del plazo
            int diasDelPlazo = durationInDays(fixedTermDepositDto.getCreationDate(), fixedTermDepositDto.getClosingDate());

            // Lógica del plazo fijo
            Double interesGanado = this.calculateInterest(fixedTermDepositDto);
            Double totalAmountToCollect = interesGanado + fixedTermDepositDto.getInvertedAmount();

            // Asignamos los resultados al DTO
            fixedTermDepositDto.setGainedInterest(interesGanado);
            fixedTermDepositDto.setTotalAmountToCollect(totalAmountToCollect);

            return fixedTermDepositDto;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto", e);
        }


    }

    public Object fixedTermDeposit(FixedTermDepositDto fixedTermDepositDto, Accounts account, User user){
        // Validar que la cuenta en pesos del usuario logueado tiene balance suficiente
        // Esto puede requerir acceso a una base de datos u otro servicio para verificar el balance

        LocalDate creationDate = LocalDate.parse(fixedTermDepositDto.getCreationDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate closingDate = LocalDate.parse(fixedTermDepositDto.getClosingDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
           if (accountService.hasBalance(account, fixedTermDepositDto.getInvertedAmount()) && this.minimumDuration(fixedTermDepositDto)){
               FixedTermDeposit fixedTerm = new FixedTermDeposit();
               fixedTerm.setAmount(fixedTermDepositDto.getInvertedAmount());
               fixedTerm.setUser(user);
               fixedTerm.setCreationDate(creationDate.atStartOfDay());
               fixedTerm.setClosingDate(closingDate.atStartOfDay());
               fixedTerm.setInterest(config.getFixedTermInterest());
               fixedTermDepositRepository.save(fixedTerm);
               accountService.updateAfterFixedTermDeposit(account, fixedTermDepositDto.getInvertedAmount());

               //TODO SACAR LA LOGICA REPETIDA
               // Lógica del plazo fijo
               Double interesGanado = this.calculateInterest(fixedTermDepositDto);
               Double totalAmountToCollect = interesGanado + fixedTermDepositDto.getInvertedAmount();
               fixedTermDepositDto.setGainedInterest(interesGanado);
               fixedTermDepositDto.setTotalAmountToCollect(totalAmountToCollect);
               return fixedTermDepositDto;
           } else{
                throw new RuntimeException("No se puede realizar el plazo fijo");
           }


    }

    public Boolean minimumDuration(FixedTermDepositDto fixedTermDepositDto){

        LocalDate creationDate = LocalDate.parse(fixedTermDepositDto.getCreationDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate closingDate = LocalDate.parse(fixedTermDepositDto.getClosingDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (ChronoUnit.DAYS.between(creationDate, closingDate) < 30) {
            throw new IllegalArgumentException("La duración mínima del plazo fijo es de 30 días");
        }else{
            return true;
        }
    }

    public Double calculateInterest(FixedTermDepositDto fixedTermDepositDto){

        Double interest = config.getFixedTermInterest();
        Double invertedAmount = fixedTermDepositDto.getInvertedAmount();
        return ((invertedAmount*interest)/100)*durationInDays(fixedTermDepositDto.getCreationDate(), fixedTermDepositDto.getClosingDate());

    }

    public Integer durationInDays(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaInicial = LocalDate.parse(startDate, formatter);
        LocalDate fechaFinal = LocalDate.parse(endDate, formatter);

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new IllegalArgumentException("La fecha final no puede ser mayor que la fecha inicial");
        }

        return (int) ChronoUnit.DAYS.between(fechaInicial, fechaFinal);
    }
    public List<FixedTermDeposit> getFixedTermDepositsByUser(Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return fixedTermDepositRepository.findByUser(user);
    }

}
