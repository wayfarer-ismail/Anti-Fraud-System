package antifraud.controller;

import antifraud.exception.BadRequestException;
import antifraud.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud/history")
public class TransactionHistoryController {

    TransactionService transactionService;

    public TransactionHistoryController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<?> listTransactions() {
        return new ResponseEntity<>(transactionService.list(), HttpStatus.OK);
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> listTransactions(@PathVariable String number) {
        try {
            List<?> transactions = transactionService.list(number);
            if (transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}