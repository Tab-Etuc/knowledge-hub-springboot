package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.WesternBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.WesternBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class WesternBookStrategy implements BookTypeStrategy {

    @Override
    public void validate(BookBean bean) {
        if (bean.getDeweyDecimalCode() == null || bean.getDeweyDecimalCode().isBlank()) {
            throw new MissingFieldException("deweyDecimalCode");
        }
        if (!bean.getDeweyDecimalCode().matches("^\\d{3}(\\.\\d+)?$")) {
            throw new WesternBookCodeInvalidException();
        }
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
            if (!bean.getDeweyDecimalCode().matches("^\\d{3}(\\.\\d+)?$")) {
                throw new WesternBookCodeInvalidException();
            }
            if (book.getWesternBookDetail() != null) {
                book.getWesternBookDetail().setDeweyDecimalCode(bean.getDeweyDecimalCode());
            }
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
