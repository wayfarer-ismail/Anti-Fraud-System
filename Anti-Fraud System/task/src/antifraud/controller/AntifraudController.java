package antifraud.controller;

import antifraud.exception.BadRequestException;
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
        try {
            SaveTransactionTuple transactionTuple = transactionService.saveTransaction(request);
            return new ResponseEntity<>(transactionTuple, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/suspicous-ip")
    public ResponseEntity<?> saveSuspiciousIp(@RequestBody String ip) {
        return new ResponseEntity<>(Map.of("ip", ip, "status", "Saved successfully!"), HttpStatus.OK);
    }

    @GetMapping("/suspicous-ip")
    public ResponseEntity<?> getSuspiciousIps() {
        return new ResponseEntity<>(Map.of("status", "OK"), HttpStatus.OK);
    }
}
