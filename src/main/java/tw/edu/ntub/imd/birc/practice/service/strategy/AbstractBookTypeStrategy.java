package tw.edu.ntub.imd.birc.practice.service.strategy;

import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;

import java.util.function.Consumer;
import java.util.function.Function;

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

    protected <T> void safeUpdateDetail(Book book,
            Function<Book, T> getter,
            Consumer<T> setter) {
        T detail = getter.apply(book);
        if (detail != null) {
            setter.accept(detail);
        }
    }
}
