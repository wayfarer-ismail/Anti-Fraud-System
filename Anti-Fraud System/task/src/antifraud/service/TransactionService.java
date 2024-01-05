package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.model.SaveTransactionTuple;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TransactionService {

    TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public SaveTransactionTuple saveTransaction(TransactionRequest transactionRequest) {
        validateTransaction(transactionRequest);
        Transaction transaction = Transaction.fromTransactionRequest(transactionRequest);

        ArrayList<String> info = new ArrayList<>(3);
        String result;
        if (transaction.getAmount() <= 200) {
            result = "ALLOWED";
            info.add("none");
        } else if (transaction.getAmount() <= 1500) {
            result = "MANUAL_PROCESSING";
            info.add("amount");
        } else {
            result = "PROHIBITED";
            info.add("amount");
        }

        transactionRepository.save(transaction);
        return new SaveTransactionTuple(result, info);
    }

    private void validateTransaction(TransactionRequest transaction) {
        if (transaction == null) {
            throw new BadRequestException("Transaction must not be null");
        }
        if (transaction.amount() == null || transaction.amount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }
        if (transaction.ip() == null || transaction.ip().isEmpty()) {
            throw new BadRequestException("IP must not be empty");
        }
        if (transaction.number() == null || transaction.number().isEmpty()) {
            throw new BadRequestException("Number must not be empty");
        }
    }
}
