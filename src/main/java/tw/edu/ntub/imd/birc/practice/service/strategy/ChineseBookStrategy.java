package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.ChineseBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.ChineseBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class ChineseBookStrategy extends AbstractBookTypeStrategy {

    private static final String CHINESE_DDC_PATTERN = "^[A-Z]\\d.*$";

    @Override
    public void validate(BookBean bean) {
        validateRequired(bean.getChineseDdcCode(), "chineseDdcCode");
        validatePattern(bean.getChineseDdcCode(), CHINESE_DDC_PATTERN, new ChineseBookCodeInvalidException());
    }

    @Override
    public String generateClassification(BookBean bean) {
        return bean.getChineseDdcCode();
    }

    @Override
    public void createDetail(Book book, BookBean bean) {
        ChineseBookDetail detail = new ChineseBookDetail();
        detail.setIsbn(book.getIsbn());
        detail.setChineseDdcCode(bean.getChineseDdcCode());
        book.setChineseBookDetail(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        if (bean.getChineseDdcCode() != null) {
            validatePattern(bean.getChineseDdcCode(), CHINESE_DDC_PATTERN, new ChineseBookCodeInvalidException());
            safeUpdateDetail(book, Book::getChineseBookDetail,
                    detail -> detail.setChineseDdcCode(bean.getChineseDdcCode()));
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        if (updateBean.getChineseDdcCode() != null) {
            fullBean.setChineseDdcCode(updateBean.getChineseDdcCode());
        }
    }

    @Override
    public BookType getType() {
        return BookType.CHINESE;
    }
}
