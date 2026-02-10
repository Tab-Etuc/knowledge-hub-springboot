package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.WesternBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.WesternBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class WesternBookStrategy extends AbstractBookTypeStrategy {

    private static final String DEWEY_DECIMAL_PATTERN = "^\\d{3}(\\.\\d+)?$";

    @Override
    public void validate(BookBean bean) {
        validateRequired(bean.getDeweyDecimalCode(), "deweyDecimalCode");
        validatePattern(bean.getDeweyDecimalCode(), DEWEY_DECIMAL_PATTERN, new WesternBookCodeInvalidException());
    }

    @Override
    public String generateClassification(BookBean bean) {
        return bean.getDeweyDecimalCode();
    }

    @Override
    public void createDetail(Book book, BookBean bean) {
        WesternBookDetail detail = new WesternBookDetail();
        detail.setIsbn(book.getIsbn());
        detail.setDeweyDecimalCode(bean.getDeweyDecimalCode());
        book.setWesternBookDetail(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        if (bean.getDeweyDecimalCode() != null) {
            validatePattern(bean.getDeweyDecimalCode(), DEWEY_DECIMAL_PATTERN, new WesternBookCodeInvalidException());
            safeUpdateDetail(book, Book::getWesternBookDetail,
                    detail -> detail.setDeweyDecimalCode(bean.getDeweyDecimalCode()));
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        if (updateBean.getDeweyDecimalCode() != null) {
            fullBean.setDeweyDecimalCode(updateBean.getDeweyDecimalCode());
        }
    }

    @Override
    public BookType getType() {
        return BookType.WESTERN;
    }
}
