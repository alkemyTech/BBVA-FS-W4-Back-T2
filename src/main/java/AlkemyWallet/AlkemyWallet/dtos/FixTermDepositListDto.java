package AlkemyWallet.AlkemyWallet.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FixTermDepositListDto {
    @NotNull
    @NotBlank
    private Long id;

    @NotNull
    @NotBlank
    private Double amount;
    @NotNull
    @NotBlank
    private Double interest;
    @NotNull
    @NotBlank
    private LocalDateTime creationDate;
    @NotNull
    @NotBlank
    private LocalDateTime closingDate;


    public FixTermDepositListDto(Long id,Double amount, Double interest, LocalDateTime creationDate, LocalDateTime closingDate) {
        this.id = id;
        this.amount = amount;
        this.interest = interest;
        this.creationDate = creationDate;
        this.closingDate = closingDate;
    }

}
