package antifraud.controller;

import antifraud.model.StolenCard;
import antifraud.service.StolenCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/stolencard")
public class StolenCardController {
    StolenCardService stolenCardService;

    public StolenCardController(StolenCardService stolenCardService) {
        this.stolenCardService = stolenCardService;
    }

    @PostMapping
    public ResponseEntity<?> saveStolenCard(@RequestBody Map<String, String> card) {
        StolenCard stolenCard = stolenCardService.saveStolenCard(card.get("number"));
        return new ResponseEntity<>(stolenCard, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getStolenCards() {
        return new ResponseEntity<>(stolenCardService.listStolenCards(), HttpStatus.OK);
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<?> deleteStolenCard(@PathVariable String number) {
        stolenCardService.deleteStolenCard(number);
        return new ResponseEntity<>(Map.of("status", "Card " + number + " successfully removed!"), HttpStatus.OK);
    }
}
