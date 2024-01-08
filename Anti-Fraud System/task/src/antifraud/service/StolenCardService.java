package antifraud.service;

import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StolenCardService {

    StolenCardRepository stolenCardRepository;

    public StolenCardService(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    public List<StolenCard> listStolenCards() {
        return stolenCardRepository.findAll();
    }

    public StolenCard saveStolenCard(String number) {
        try {
            validateLuhnAlgorithm(number);
            return stolenCardRepository.save(new StolenCard(number));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Card already exists");
        }
    }

    @Transactional
    public void deleteStolenCard(String number) {
        validateLuhnAlgorithm(number);
        int count = stolenCardRepository.deleteByNumber(number);
        if (count == 0) {
            throw new NotFoundException("Card number not found");
        }
    }

    private void validateLuhnAlgorithm(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = n % 10 + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        if (sum % 10 != 0) {
            throw new BadRequestException("Card number is not valid");
        }
    }

    public boolean isStolen(String number) {
        return stolenCardRepository.existsByNumber(number);
    }
}
