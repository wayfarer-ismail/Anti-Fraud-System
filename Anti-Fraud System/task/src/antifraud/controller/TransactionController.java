package antifraud.controller;

import antifraud.exception.BadRequestException;
import antifraud.model.SaveTransactionTuple;
import antifraud.model.request.TransactionRequest;
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
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        try {
            SaveTransactionTuple transactionTuple = transactionService.saveTransaction(request);
            return new ResponseEntity<>(transactionTuple, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
