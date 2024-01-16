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

    /**
     * Deletes an IP address from the database.
     * If the IP address exists, it deletes it and returns the number of deleted IP addresses.
     *
     * @throws NotFoundException if the IP address does not exist
     */
    @Transactional
    public Integer deleteSuspiciousIp(String ip) {
        validateIp(ip);
        if (!ipRepository.existsByIp(ip)) {
            throw new NotFoundException("IP does not exist!");
        }
        return ipRepository.deleteByIp(ip);
    }

    /**
     * Saves an IP address to the database.
     * @throws ConflictException if the IP address already exists
     */
    @Transactional
    public Ip saveSuspiciousIp(String ip) {
        validateIp(ip);
        if (ipRepository.existsByIp(ip)) {
            throw new ConflictException("IP already exists!");
        }
        return ipRepository.save(new Ip(ip));
    }

    /**
     * validates the format of an IP address.
     * It splits the IP address into parts using the dot as a separator.
     * If the IP address does not have exactly four parts, it throws a BadRequestException.
     * It then tries to parse each part of the IP address as an integer.
     * If any part cannot be parsed as an integer or is not in the range 0-255,
     *      it throws a BadRequestException.
     *
     * @throws BadRequestException if the IP address is invalid
     */
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

    public boolean isSuspicious(String ip) {
        return ipRepository.existsByIp(ip);
    }
}
