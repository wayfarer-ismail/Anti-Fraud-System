package antifraud.repository;

import antifraud.model.TransactionMaxValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMaxValuesRepository extends JpaRepository<TransactionMaxValues, Long> {
}
