package antifraud.controller;

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

    @GetMapping
    public ResponseEntity<?> listTransactions() {
        return new ResponseEntity<>(List.of(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable String id) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
