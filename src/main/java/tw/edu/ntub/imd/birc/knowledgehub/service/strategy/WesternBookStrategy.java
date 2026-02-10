package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.WesternBookDetail;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.WesternBookCodeInvalidException;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

import java.util.Optional;

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
        Optional.ofNullable(updateBean.getDeweyDecimalCode()).ifPresent(fullBean::setDeweyDecimalCode);
    }

    @Override
    public void populateBean(Book book, BookBean bean) {
        if (book.getWesternBookDetail() != null) {
            bean.setDeweyDecimalCode(book.getWesternBookDetail().getDeweyDecimalCode());
        }
    }

    @Override
    public void addResponseFields(BookBean bean, ObjectData data) {
        data.add("deweyDecimalCode", bean.getDeweyDecimalCode());
    }

    @Override
    public BookType getType() {
        return BookType.WESTERN;
    }
}
