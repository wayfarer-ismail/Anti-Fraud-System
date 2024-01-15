package antifraud.controller;

import antifraud.model.Transaction;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud/transaction")
public class TransactionController {
    TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.saveTransaction(request);
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateTransactionFeedback(@RequestBody TransactionFeedbackRequest request) {
        Transaction transactionResponse = transactionService.updateTransactionFeedback(request);
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }
}
