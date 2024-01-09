package antifraud.model.response;

import java.util.List;

public record TransactionResponse(String result, String info) {

    public TransactionResponse(String result, List<String> info) {
        this(result, String.join(", ", info));
    }
}
