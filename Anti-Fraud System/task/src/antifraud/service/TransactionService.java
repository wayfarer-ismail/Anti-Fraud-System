package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.exception.UnprocessableEntityException;
import antifraud.model.Transaction;
import antifraud.model.enums.Feedback;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static antifraud.model.enums.Feedback.*;

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
        validateFields(transactionRequest);

        Transaction transaction = Transaction.fromTransactionRequest(transactionRequest);
        TransactionResponse response = resolveTransaction(transaction);

        transaction.setResult(response.result());
        transactionRepository.save(transaction);
        return response;
    }

    private TransactionResponse resolveTransaction(Transaction transaction) {
        List<Transaction> transactions = transactionRepository.findByNumber(transaction.getNumber());
        ArrayList<String> info = new ArrayList<>(3);
        Feedback result;

        // check if the card number is associated with transactions from more than two regions in the past hour
        if (countDistinctRegions(transaction, transactions) > 2) {
            info.add("region-correlation");
        }

        // if card number has been used by more than 2 other ip addresses in the past hour
        if (countDistinctIp(transaction, transactions) > 2) {
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
            if (countDistinctRegions(transaction, transactions) > 1) {
                info.add("region-correlation");
            }

            if (countDistinctIp(transaction, transactions) > 1) {
                info.add("ip-correlation");
            }
            if (transaction.getAmount() > 200) {
                info.add("amount");
            }

            // if not passed for manual processing
            if (info.isEmpty()) {
                result = ALLOWED;
                info.add("none");
            } else {
                result = MANUAL_PROCESSING;
            }
        } else {
            result = PROHIBITED;
        }

        return new TransactionResponse(result, info.stream().sorted().toList());
    }

    private long countTransactionsFromDistinct(Transaction transaction,
                                               List<Transaction> transactions,
                                               Predicate<Transaction> filter,
                                               Function<Transaction, Object> groupBy) {
        LocalDateTime oneHourAgo = transaction.getDate().minusHours(1);
        LocalDateTime current = transaction.getDate();

        return  transactions.stream()
                .filter(t -> t.getDate().isAfter(oneHourAgo) && t.getDate().isBefore(current))
                .filter(filter)
                .map(groupBy)
                .distinct()
                .count();
    }

    private long countDistinctRegions(Transaction transaction, List<Transaction> transactions) {
        return countTransactionsFromDistinct(transaction, transactions, t -> !t.getRegion().equals(transaction.getRegion()), Transaction::getRegion);
    }

    private long countDistinctIp(Transaction transaction, List<Transaction> transactions) {
        return countTransactionsFromDistinct(transaction, transactions, t -> !t.getIp().equals(transaction.getIp()), Transaction::getIp);
    }

    private void validateFields(TransactionRequest transaction) {
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

    public Transaction updateTransactionFeedback(TransactionFeedbackRequest request) {
        Transaction transaction = transactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (transaction.hasFeedback()) {
            throw new ConflictException("Feedback already given");
        }

        Feedback feedback = Feedback.valueOf(request.feedback());
        if (feedback == transaction.getResult()) {
            throw new UnprocessableEntityException("Feedback must not be equal to result");
        }
        transaction.setFeedback(feedback);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }

    public List<Transaction> list(String number) {
        StolenCardService.validateLuhnAlgorithm(number);
        return transactionRepository.findByNumber(number);
    }
}
