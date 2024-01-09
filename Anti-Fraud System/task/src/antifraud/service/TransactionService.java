package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class TransactionService {

    TransactionRepository transactionRepository;
    IpService ipService;
    StolenCardService stolenCardService;

    public TransactionService(TransactionRepository transactionRepository, IpService ipService, StolenCardService stolenCardService) {
        this.transactionRepository = transactionRepository;
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
    }

    public TransactionResponse saveTransaction(TransactionRequest transactionRequest) {
        validateTransaction(transactionRequest);

        Transaction transaction = Transaction.fromTransactionRequest(transactionRequest);
        transactionRepository.save(transaction);
        return resolveTransaction(transaction);
    }

    private TransactionResponse resolveTransaction(Transaction transaction) {
        List<Transaction> transactions = transactionRepository.findByNumber(transaction.getNumber());
        ArrayList<String> info = new ArrayList<>(3);
        String result;

        // check if the card number is associated with transactions from more than two regions in the past hour
        if (transactionsFromNRegions(transaction, transactions) > 2) {
            info.add("region-correlation");
        }

        // if card number has been used by more than 2 other ip addresses in the past hour
        if (transactionsFromNIpAddresses(transaction, transactions) > 2) {
            info.add("ip-correlation");
        }

        if (stolenCardService.isStolen(transaction.getNumber())) {
            info.add("card-number");
        }

        if (ipService.isSuspicious(transaction.getIp())) {
            info.add("ip");
        }

        if (transaction.getAmount()> 1500) {
            info.add("amount");
        }

        // if not prohibited
        if (info.isEmpty()) {
            if (transactionsFromNRegions(transaction, transactions) > 1) {
                info.add("region-correlation");
            }
            if (transactionsFromNIpAddresses(transaction, transactions) > 1) {
                info.add("ip-correlation");
            }
            if (transaction.getAmount() > 200) {
                info.add("amount");
            }

            // if not passed for manual processing
            if (info.isEmpty()) {
                result = "ALLOWED";
                info.add("none");
            } else {
                result = "MANUAL_PROCESSING";
            }
        } else {
            result = "PROHIBITED";
        }

        return new TransactionResponse(result, info.stream().sorted().toList());
    }

    private long transactionsFromN(List<Transaction> transactions, Predicate<Transaction> filter) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        return  transactions.stream()
                .filter(t -> t.getDateTime().isAfter(oneHourAgo))
                .filter(filter)
                .count();
    }

    private long transactionsFromNRegions(Transaction transaction, List<Transaction> transactions) {
        return transactionsFromN(transactions, t -> !t.getRegion().equals(transaction.getRegion()));
    }

    private long transactionsFromNIpAddresses(Transaction transaction, List<Transaction> transactions) {
        return transactionsFromN(transactions, t -> !t.getIp().equals(transaction.getIp()));
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
