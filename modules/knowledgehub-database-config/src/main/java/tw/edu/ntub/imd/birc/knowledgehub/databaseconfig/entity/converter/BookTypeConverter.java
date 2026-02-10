package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.converter;

import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BookTypeConverter implements AttributeConverter<BookType, String> {
    @Override
    public String convertToDatabaseColumn(BookType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public BookType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return BookType.of(dbData);
    }
}
