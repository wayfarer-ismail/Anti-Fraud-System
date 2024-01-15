package antifraud.controller;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.Ip;
import antifraud.service.IpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class SuspiciousIpController {
    IpService ipService;

    public SuspiciousIpController(IpService ipService) {
        this.ipService = ipService;
    }

    @PostMapping
    public ResponseEntity<?> saveSuspiciousIp(@RequestBody Map<String, String> request) {
        try {
            Ip savedIp = ipService.saveSuspiciousIp(request.get("ip"));
            return new ResponseEntity<>(savedIp, HttpStatus.OK);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getSuspiciousIps() {
        return new ResponseEntity<>(ipService.listSuspiciousIps(), HttpStatus.OK);
    }

    @DeleteMapping("/{ip}")
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
}
