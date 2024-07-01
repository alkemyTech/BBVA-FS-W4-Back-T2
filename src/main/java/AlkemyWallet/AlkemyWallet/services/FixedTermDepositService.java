package AlkemyWallet.AlkemyWallet.services;

// Imports omitidos para mayor claridad

import AlkemyWallet.AlkemyWallet.config.FixedTermDepositConfig;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.FixTermDepositListDto;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;


import AlkemyWallet.AlkemyWallet.exceptions.InvalidDateOrderException;
import AlkemyWallet.AlkemyWallet.exceptions.MinimumDurationException;
import AlkemyWallet.AlkemyWallet.repositories.FixedTermDepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class FixedTermDepositService {

    private final UserService userService;
    FixedTermDepositConfig config;
    AccountService accountService;
    JwtService jwtService;
    FixedTermDepositRepository fixedTermDepositRepository;

    public FixedTermDepositDto simulateFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        LocalDate fechaInicial = LocalDate.parse(fixedTermDepositDto.getCreationDate(), formatter);
        LocalDate fechaFinal = LocalDate.parse(fixedTermDepositDto.getClosingDate(), formatter);

        LocalDateTime fechaYHoraInicial = LocalDateTime.of(fechaInicial, LocalDateTime.now().toLocalTime());
        LocalDateTime fechaYHoraFinal = LocalDateTime.of(fechaFinal, LocalDateTime.now().toLocalTime());

        if (fechaYHoraInicial.isAfter(fechaYHoraFinal)) {
            // Lanzar una RuntimeException con un mensaje adecuado
            throw new InvalidDateOrderException("La fecha final no puede ser mayor que la fecha inicial");
        }
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

    public Object fixedTermDeposit(FixedTermDepositDto fixedTermDepositDto, Accounts account, User user) {

        LocalDate creationDate = LocalDate.parse(fixedTermDepositDto.getCreationDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate closingDate = LocalDate.parse(fixedTermDepositDto.getClosingDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (accountService.hasBalance(account, fixedTermDepositDto.getInvertedAmount()) && this.minimumDuration(fixedTermDepositDto)) {
            FixedTermDeposit fixedTerm = new FixedTermDeposit();
            fixedTerm.setAmount(fixedTermDepositDto.getInvertedAmount());
            fixedTerm.setUser(user);
            fixedTerm.setCreationDate(creationDate.atStartOfDay());
            fixedTerm.setClosingDate(closingDate.atStartOfDay());
            fixedTerm.setInterest(config.getFixedTermInterest());
            fixedTermDepositRepository.save(fixedTerm);
            accountService.updateAccountBalance(account, fixedTermDepositDto.getInvertedAmount());


            // Lógica del plazo fijo
            Double interesGanado = this.calculateInterest(fixedTermDepositDto);
            Double totalAmountToCollect = interesGanado + fixedTermDepositDto.getInvertedAmount();
            fixedTermDepositDto.setGainedInterest(interesGanado);
            fixedTermDepositDto.setTotalAmountToCollect(totalAmountToCollect);
            return fixedTermDepositDto;
        } else {
            throw new RuntimeException("No se puede realizar el plazo fijo");
        }


    }

    public Boolean minimumDuration(FixedTermDepositDto fixedTermDepositDto) {

        LocalDate creationDate = LocalDate.parse(fixedTermDepositDto.getCreationDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate closingDate = LocalDate.parse(fixedTermDepositDto.getClosingDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (ChronoUnit.DAYS.between(creationDate, closingDate) < 30) {
            throw new MinimumDurationException("La duración mínima del plazo fijo es de 30 días");
        } else {
            return true;
        }
    }

    public Double calculateInterest(FixedTermDepositDto fixedTermDepositDto) {

        Double interest = config.getFixedTermInterest();
        Double invertedAmount = fixedTermDepositDto.getInvertedAmount();
        return ((invertedAmount * interest) / 100) * durationInDays(fixedTermDepositDto.getCreationDate(), fixedTermDepositDto.getClosingDate());

    }

    public Integer durationInDays(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaInicial = LocalDate.parse(startDate, formatter);
        LocalDate fechaFinal = LocalDate.parse(endDate, formatter);

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new InvalidDateOrderException("La fecha final no puede ser mayor que la fecha inicial");
        }

        return (int) ChronoUnit.DAYS.between(fechaInicial, fechaFinal);
    }
    public List<FixedTermDeposit> getFixedTermDepositsByUser(Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return fixedTermDepositRepository.findByUser(user);
    }

    public List<FixTermDepositListDto> getFixedTermDepositsByUserDTO(Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositRepository.findByUser(user);

        return fixedTermDeposits.stream()
                .map(deposit -> new FixTermDepositListDto(deposit.getId(),deposit.getAmount(), deposit.getInterest(), deposit.getCreationDate(), deposit.getClosingDate()))
                .collect(Collectors.toList());
    }


    public Double getUnclosedDepositsTotalSum(Long userId) {
        List<FixedTermDeposit> fixedTermDeposits = getFixedTermDepositsByUser(userId);

        Double unclosedDepositsTotalSum = fixedTermDeposits.stream()
                .filter(deposit -> deposit.getClosingDate() == null || deposit.getClosingDate().isAfter(LocalDateTime.now()))
                .mapToDouble(deposit -> deposit.getAmount() + calculateEarnedInterest(deposit))
                .sum();

        return unclosedDepositsTotalSum;
    }

    public Double calculateEarnedInterest(FixedTermDeposit deposit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDate = deposit.getCreationDate();
        LocalDateTime closingDate = deposit.getClosingDate() != null ? deposit.getClosingDate() : now;

        // Si el depósito está cerrado, no ganará más intereses
        if (deposit.getClosingDate() != null && deposit.getClosingDate().isBefore(now)) {
            return 0.0;
        }

        int daysBetween = (int) ChronoUnit.DAYS.between(creationDate, now);

        Double interestRate = deposit.getInterest();
        Double amount = deposit.getAmount();

        return ((amount * interestRate) / 100) * daysBetween;
    }
}

