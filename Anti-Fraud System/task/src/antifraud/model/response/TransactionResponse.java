package antifraud.model.response;

import java.util.List;

public record TransactionResponse(String result, List<String> info) {
}
