package antifraud.model.response;

import antifraud.model.enums.Feedback;

import java.util.List;

public record TransactionResponse(Feedback result, String info) {

    public TransactionResponse(Feedback result, List<String> info) {
        this(result, String.join(", ", info));
    }
}
