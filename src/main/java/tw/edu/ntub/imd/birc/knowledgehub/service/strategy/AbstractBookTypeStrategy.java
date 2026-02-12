package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import tw.edu.ntub.imd.birc.knowledgehub.exception.form.MissingFieldException;

public abstract class AbstractBookTypeStrategy implements BookTypeStrategy {

    protected void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new MissingFieldException(fieldName);
        }
        if (value instanceof String && ((String) value).isBlank()) {
            throw new MissingFieldException(fieldName);
        }
    }

    protected void validatePattern(String value, String pattern, RuntimeException exception) {
        if (!value.matches(pattern)) {
            throw exception;
        }
    }

}
