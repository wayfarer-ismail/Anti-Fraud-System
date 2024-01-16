package antifraud.controller;

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
        Ip savedIp = ipService.saveSuspiciousIp(request.get("ip"));
        return new ResponseEntity<>(savedIp, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getSuspiciousIps() {
        return new ResponseEntity<>(ipService.listSuspiciousIps(), HttpStatus.OK);
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<?> deleteSuspiciousIp(@PathVariable String ip) {
        ipService.deleteSuspiciousIp(ip);
        return new ResponseEntity<>(Map.of("status", "IP " + ip + " successfully removed!"), HttpStatus.OK);
    }
}
