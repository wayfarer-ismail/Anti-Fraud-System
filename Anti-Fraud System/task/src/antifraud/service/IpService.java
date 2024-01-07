package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.Ip;
import antifraud.repository.IpRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public Integer deleteSuspiciousIp(String ip) {
        validateIp(ip);
        if (!ipRepository.existsByIp(ip)) {
            throw new NotFoundException("IP does not exist!");
        }
        return ipRepository.deleteByIp(ip);
    }

    public Ip saveSuspiciousIp(String ip) {
        validateIp(ip);
        if (ipRepository.existsByIp(ip)) {
            throw new ConflictException("IP already exists!");
        }
        return ipRepository.save(new Ip(ip));
    }

    private void validateIp(String ip) {
        String[] ipParts = ip.split("\\.");
        if (ipParts.length != 4) {
            throw new BadRequestException("IP is invalid!");
        }
        for (String ipPart : ipParts) {
            try {
                int ipPartInt = Integer.parseInt(ipPart);
                if (ipPartInt < 0 || ipPartInt > 255) {
                    throw new BadRequestException("IP is invalid!");
                }
            } catch (NumberFormatException e) {
                throw new BadRequestException("IP is invalid!");
            }
        }
    }
}
