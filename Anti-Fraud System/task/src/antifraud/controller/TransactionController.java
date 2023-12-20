package antifraud.controller;

import antifraud.model.TransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransactionController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        double amount = request.amount();
        if (amount <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (amount <= 200) {
            return new ResponseEntity<>(Map.of("result", "ALLOWED"), HttpStatus.OK);
        } else if (amount <= 1500) {
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("result", "PROHIBITED"), HttpStatus.OK);
        }
    }
}

