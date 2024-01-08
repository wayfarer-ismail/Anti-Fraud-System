package antifraud.controller;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.Ip;
import antifraud.model.SaveTransactionTuple;
import antifraud.model.StolenCard;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.IpRequest;
import antifraud.service.IpService;
import antifraud.service.StolenCardService;
import antifraud.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class AntifraudController {

    TransactionService transactionService;
    IpService ipService;
    StolenCardService stolenCardService;

    public AntifraudController(TransactionService transactionService, IpService ipService, StolenCardService stolenCardService) {
        this.transactionService = transactionService;
        this.ipService = ipService;
        this.stolenCardService = stolenCardService;
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

    @PostMapping("/suspicious-ip")
    public ResponseEntity<?> saveSuspiciousIp(@RequestBody IpRequest ip) {
        try {
            Ip savedIp = ipService.saveSuspiciousIp(ip.getIp());
            return new ResponseEntity<>(savedIp, HttpStatus.OK);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stolencard")
    public ResponseEntity<?> saveStolenCard(@RequestBody Map<String, String> card) {
        try {
            StolenCard stolenCard = stolenCardService.saveStolenCard(card.get("number"));
            return new ResponseEntity<>(stolenCard, HttpStatus.OK);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<?> getSuspiciousIps() {
        return new ResponseEntity<>(ipService.listSuspiciousIps(), HttpStatus.OK);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<?> getStolenCards() {
        return new ResponseEntity<>(stolenCardService.listStolenCards(), HttpStatus.OK);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteSuspiciousIp(@PathVariable String ip) {
        try {
            ipService.deleteSuspiciousIp(ip);
            return new ResponseEntity<>(Map.of("status", "IP " + ip + " successfully removed!"), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<?> deleteStolenCard(@PathVariable String number) {
        try {
            stolenCardService.deleteStolenCard(number);
            return new ResponseEntity<>(Map.of("status", "Card " + number + " successfully removed!"), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
