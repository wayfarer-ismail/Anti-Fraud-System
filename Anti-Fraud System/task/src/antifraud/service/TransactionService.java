package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.exception.UnprocessableEntityException;
import antifraud.model.Transaction;
import antifraud.model.TransactionMaxValues;
import antifraud.model.enums.Feedback;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.TransactionMaxValuesRepository;
import antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static antifraud.model.enums.Feedback.*;

@Service
public class TransactionService {
    TransactionRepository transactionRepository;
    TransactionMaxValuesRepository transactionMaxValuesRepository;
    IpService ipService;
    StolenCardService stolenCardService;
    double maxAllow = 200;
    double maxManual = 1500;

    /**
     * Constructor for the TransactionService class.
     * Initializes the TransactionRepository, IpService, StolenCardService, and TransactionMaxValuesRepository instances.
     * Also sets the maximum allowed and manual transaction amounts.
     *
     * @param transactionRepository The repository for transactions.
     * @param ipService The service for IP-related operations.
     * @param stolenCardService The service for stolen card-related operations.
     * @param transactionMaxValuesRepository The repository for transaction max values.
     */
    public TransactionService(TransactionRepository transactionRepository,
                              IpService ipService,
                              StolenCardService stolenCardService,
                              TransactionMaxValuesRepository transactionMaxValuesRepository) {
        this.transactionRepository = transactionRepository;
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
        this.transactionMaxValuesRepository = transactionMaxValuesRepository;

        TransactionMaxValues maxValues = transactionMaxValuesRepository
                .findById(1L).orElse(new TransactionMaxValues(maxAllow, maxManual));
        maxAllow = maxValues.getMaxAllow();
        maxManual = maxValues.getMaxManual();
    }

    @Transactional
    public TransactionResponse saveTransaction(TransactionRequest transactionRequest) {
        validateFields(transactionRequest);

        Transaction transaction = Transaction.fromTransactionRequest(transactionRequest);
        TransactionResponse response = resolveTransaction(transaction);

        transaction.setResult(response.result());
        transactionRepository.save(transaction);
        return response;
    }

    /**
     * Resolves a transaction.
     * Retrieves transactions with the same card number, applies rules to the transaction,
     *  and returns a response based on the rules.
     */
    private TransactionResponse resolveTransaction(Transaction transaction) {
        List<Transaction> transactions = transactionRepository.findByNumber(transaction.getNumber());
        ArrayList<String> info = new ArrayList<>(3);

        Feedback result = applyRules(transaction, transactions, info);

        return new TransactionResponse(result, info.stream().sorted().toList());
    }

    /**
     * Applies rules to a transaction.
     * Checks various conditions and adds information to the info list based on these conditions.
     * Returns a feedback based on the conditions.
     *
     * @param transaction The transaction to apply rules to.
     * @param transactions The list of transactions with the same card number.
     * @param info The list to add information to.
     * @return The feedback of the transaction.
     */
    private Feedback applyRules(Transaction transaction, List<Transaction> transactions, ArrayList<String> info) {
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

        if (transaction.getAmount()> maxManual) {
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
            if (transaction.getAmount() > maxAllow) {
                info.add("amount");
            }

            // if not passed for manual processing
            if (info.isEmpty()) {
                info.add("none");
                return ALLOWED;
            } else {
                return MANUAL_PROCESSING;
            }
        } else {
            return PROHIBITED;
        }
    }

    /**
     * Counts transactions based on distinct occurrences of a specified field within the past hour.
     * Filters transactions based on a given filter and groups them by a
     *      given groupBy function which is for mapping a getter.
     * Returns the count of distinct groups.
     *
     * @param transaction The transaction to set time based on.
     * @param transactions The list of transactions to count from.
     * @param filter The filter to apply to the transactions.
     * @param groupBy The function to group the transactions by (a getter).
     * @return The count of distinct groups.
     */
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
        return countTransactionsFromDistinct(transaction, transactions,
                t -> !t.getRegion().equals(transaction.getRegion()), Transaction::getRegion);
    }

    private long countDistinctIp(Transaction transaction, List<Transaction> transactions) {
        return countTransactionsFromDistinct(transaction, transactions,
                t -> !t.getIp().equals(transaction.getIp()), Transaction::getIp);
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

    /**
     * Updates the feedback of a transaction.
     * Retrieves the transaction by its ID, validates the feedback, sets the feedback,
     *  adjusts the max values, and saves the transaction to the database.
     *
     * @param request The request to update the transaction feedback.
     * @return The updated transaction.
     * @throws NotFoundException if the transaction does not exist
     * @throws UnprocessableEntityException if the feedback is equal to the result
     * @throws ConflictException if the transaction already has feedback
     * @see #adjustMaxValues(Transaction)
     */
    @Transactional
    public Transaction updateTransactionFeedback(TransactionFeedbackRequest request) {
        Transaction transaction = transactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        Feedback feedback = Feedback.valueOf(request.feedback());
        if (feedback == transaction.getResult()) {
            throw new UnprocessableEntityException("Feedback must not be equal to result");
        }

        if (transaction.hasFeedback()) {
            throw new ConflictException("Feedback already given");
        }

        transaction.setFeedback(feedback);
        adjustMaxValues(transaction);
        return transactionRepository.save(transaction);
    }

    /**
     * Adjusts the max values based on a transaction.
     * Adjusts the max allowed and manual transaction amounts based on the feedback and
     *  result of the transaction.
     * Saves the new max values to the database.
     *
     * @param transaction The transaction to adjust the max values based on.
     */
    private void adjustMaxValues(Transaction transaction) {
        Feedback validity = transaction.getResult();
        Feedback feedback = Feedback.valueOf(transaction.getFeedback());

        if (validity == ALLOWED) {
            if (feedback == PROHIBITED) {
                maxManual = Math.ceil(0.8 * maxManual - 0.2 * transaction.getAmount());
            }
            maxAllow = Math.ceil(0.8 * maxAllow - 0.2 * transaction.getAmount());

        } else if (validity == MANUAL_PROCESSING) {
            if (feedback == ALLOWED) {
                maxAllow = Math.ceil(0.8 * maxAllow + 0.2 * transaction.getAmount());
            } else if (feedback == PROHIBITED) {
                maxManual = Math.ceil(0.8 * maxManual - 0.2 * transaction.getAmount());
            }

        } else if (validity == PROHIBITED) {
            if (feedback == ALLOWED) {
                maxAllow = Math.ceil(0.8 * maxAllow + 0.2 * transaction.getAmount());
            }
            maxManual = Math.ceil(0.8 * maxManual + 0.2 * transaction.getAmount());
        }

        // save new max values to database for persistence
        TransactionMaxValues settings = transactionMaxValuesRepository
                .findById(1L).orElse(new TransactionMaxValues(maxAllow, maxManual));
        settings.setMaxAllow(maxAllow);
        settings.setMaxManual(maxManual);
        transactionMaxValuesRepository.save(settings);
    }

    public List<Transaction> listAll() {
        return transactionRepository.findAll();
    }

    /**
     * Lists transactions by card number.
     * Validates the card number using the Luhn algorithm and returns the transactions
     *  with the specified card number.
     *
     * @param number The card number to list transactions by.
     * @return The list of transactions with the specified card number.
     * @see StolenCardService#validateLuhnAlgorithm(String)
     */
    public List<Transaction> listByNumber(String number) {
        StolenCardService.validateLuhnAlgorithm(number);
        return transactionRepository.findByNumber(number);
    }
}
