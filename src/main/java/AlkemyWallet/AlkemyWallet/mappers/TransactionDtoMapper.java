package AlkemyWallet.AlkemyWallet.mappers;

import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.TransactionBalanceDTO;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class TransactionDtoMapper {
    private final ModelMapper modelMapper;

    public TransactionBalanceDTO mapToTransactionBalanceDto(Transaction transaction) {
        TransactionBalanceDTO transactionDtoResponse = modelMapper.map(transaction, TransactionBalanceDTO.class);

        // Mapear el destino
        transactionDtoResponse.setDestino(transaction.getAccountId().getCBU());

        // Mapear el tipo de transacción
        transactionDtoResponse.setType(TransactionEnum.valueOf(transaction.getType().toString()));

        // Mapear la moneda
        transactionDtoResponse.setCurrency(transaction.getOriginAccount().getCurrency().toString());

        transactionDtoResponse.setTransactionDate(transaction.getTransactionDate());

        return transactionDtoResponse;
    }
}
