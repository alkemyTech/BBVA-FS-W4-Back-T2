package AlkemyWallet.AlkemyWallet.mappers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Component
public class TransactionResponseMapper {

    private final ModelMapper modelMapper;

    public TransactionResponse mapToTransactionResponse(Transaction transaction, Accounts originAccount, Accounts destinationAccount) {
        TransactionResponse transactionResponse = modelMapper.map(transaction, TransactionResponse.class);

        // Mapear el destino
        transactionResponse.setDestino(destinationAccount.getCBU());

        // Mapear el origen
        transactionResponse.setOrigen(originAccount.getCBU());

        // Mapear la fecha de transacción
        transactionResponse.setFechaDeTransaccion(LocalDate.now());

        // Mapear el tipo de transacción
        transactionResponse.setTipoDeTransaccion(TransactionEnum.valueOf(transaction.getType().toString()));

        // Mapear la moneda
        transactionResponse.setCurrency(originAccount.getCurrency().toString());

        return transactionResponse;
    }
}
