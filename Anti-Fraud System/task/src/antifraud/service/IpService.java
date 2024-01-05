package antifraud.service;

import antifraud.model.Ip;
import antifraud.repository.IpRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IpService {

    IpRepository ipRepository;

    public IpService(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    public List<Ip> listSuspiciousIps() {
        return ipRepository.findAll();
    }

    public Integer deleteSuspiciousIp(String ip) {
        return ipRepository.deleteByIp(ip);
    }

    public Ip saveSuspiciousIp(String ip) {
        return ipRepository.save(new Ip(ip));
    }
}
