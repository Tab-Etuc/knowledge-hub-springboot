package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.ChineseBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.ChineseBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class ChineseBookStrategy implements BookTypeStrategy {

    @Override
    public void validate(BookBean bean) {
        if (bean.getChineseDdcCode() == null || bean.getChineseDdcCode().isBlank()) {
            throw new MissingFieldException("chineseDdcCode");
        }
        if (!bean.getChineseDdcCode().matches("^[A-Z]\\d.*$")) {
            throw new ChineseBookCodeInvalidException();
        }
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
            if (!bean.getChineseDdcCode().matches("^[A-Z]\\d.*$")) {
                throw new ChineseBookCodeInvalidException();
            }
            if (book.getChineseBookDetail() != null) {
                book.getChineseBookDetail().setChineseDdcCode(bean.getChineseDdcCode());
            }
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
