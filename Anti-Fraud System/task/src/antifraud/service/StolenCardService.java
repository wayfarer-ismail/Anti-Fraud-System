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

    /**
     * This method saves a new stolen card to the database.
     * It first validates the card number using the Luhn algorithm.
     * If the card number is valid and not already in the database, it saves the card.
     * If the card number is already in the database, it throws a ConflictException.
     * @return the saved StolenCard object
     */
    @Transactional
    public StolenCard saveStolenCard(String number) {
        try {
            validateLuhnAlgorithm(number);
            return stolenCardRepository.save(new StolenCard(number));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Card already exists");
        }
    }

    /**
     * This method deletes a stolen card from the database.
     * It first validates the card number using the Luhn algorithm.
     * If the card number is valid and exists in the database, it deletes the card.
     *
     * @throws NotFoundException if the card number does not exist
     */
    @Transactional
    public void deleteStolenCard(String number) {
        validateLuhnAlgorithm(number);
        int count = stolenCardRepository.deleteByNumber(number);
        if (count == 0) {
            throw new NotFoundException("Card number not found");
        }
    }

    /**
     * This is a static method that validates a card number using the Luhn algorithm.
     * @throws BadRequestException if the card number is not valid
     */
    public static void validateLuhnAlgorithm(String number) {
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
