package antifraud.controller;

import antifraud.model.SaveTransactionTuple;
import antifraud.model.request.TransactionRequest;
import antifraud.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class AntifraudController {

    TransactionService transactionService;

    public AntifraudController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        SaveTransactionTuple transactionTuple = transactionService.saveTransaction(request);
        return new ResponseEntity<>(transactionTuple, HttpStatus.OK);
    }

    @PostMapping("/{ip}")
    public ResponseEntity<?> saveSuspiciousIp(@PathVariable String ip) {
        return new ResponseEntity<>(Map.of("ip", ip, "status", "Saved successfully!"), HttpStatus.OK);
    }
}
