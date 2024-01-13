package antifraud.model.enums;

public enum Feedback {
    ALLOWED,
    MANUAL_PROCESSING,
    PROHIBITED,
    NONE{
        @Override
        public String toString() { return ""; }
    }
}
