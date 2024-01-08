package antifraud.service;

import antifraud.exception.ConflictException;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
            return stolenCardRepository.save(new StolenCard(number));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Card already exists");
        }
    }

    public void deleteStolenCard(String number) {
        stolenCardRepository.deleteByNumber(number);
    }
}
