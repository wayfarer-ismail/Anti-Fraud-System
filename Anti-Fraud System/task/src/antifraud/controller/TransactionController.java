package antifraud.controller;

import antifraud.model.ResponseData;
import antifraud.model.TransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<ResponseData> createTransaction(@RequestBody TransactionRequest request) {
        double amount = request.getAmount();
        if (amount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData("<String>"));
        } else if (amount <= 200) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData("ALLOWED"));
        } else if (amount <= 1500) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData("MANUAL_PROCESSING"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseData("PROHIBITED"));
        }
    }
}

