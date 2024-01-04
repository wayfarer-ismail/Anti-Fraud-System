package antifraud.model;

import java.util.List;

public record SaveTransactionTuple(String result, String info) {

    public SaveTransactionTuple(String result, List<String> info) {
        this(result, String.join(", ", info));
    }
}
