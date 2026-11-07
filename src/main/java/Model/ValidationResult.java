package Model;

import java.util.List;

/**
 * The outcome of checking a {@link GameConfig}.
 */
public final class ValidationResult {

    private final List<String> errors;

    private ValidationResult(List<String> errors) {
        this.errors = List.copyOf(errors);
    }

    public static ValidationResult ok() {
        return new ValidationResult(List.of());
    }

    public static ValidationResult of(List<String> errors) {
        return new ValidationResult(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return String.join("\n", errors);
    }
}
