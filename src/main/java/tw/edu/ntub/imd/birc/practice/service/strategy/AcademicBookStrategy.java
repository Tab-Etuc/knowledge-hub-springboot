package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.AcademicBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.AcademicBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class AcademicBookStrategy implements BookTypeStrategy {

    @Override
    public void validate(BookBean bean) {
        if (bean.getLcClassMark() == null || bean.getLcClassMark().isBlank()) {
            throw new MissingFieldException("lcClassMark");
        }
        if (!bean.getLcClassMark().matches("^[A-Z]{1,2}\\d.*$")) {
            throw new AcademicBookCodeInvalidException();
        }
    }

    @Override
    public String generateClassification(BookBean bean) {
        return bean.getLcClassMark();
    }

    @Override
    public void createDetail(Book book, BookBean bean) {
        AcademicBookDetail detail = new AcademicBookDetail();
        detail.setIsbn(book.getIsbn());
        detail.setLcClassMark(bean.getLcClassMark());
        book.setAcademicBookDetail(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        if (bean.getLcClassMark() != null) {
            if (!bean.getLcClassMark().matches("^[A-Z]{1,2}\\d.*$")) {
                throw new AcademicBookCodeInvalidException();
            }
            if (book.getAcademicBookDetail() != null) {
                book.getAcademicBookDetail().setLcClassMark(bean.getLcClassMark());
            }
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        if (updateBean.getLcClassMark() != null) {
            fullBean.setLcClassMark(updateBean.getLcClassMark());
        }
    }

    @Override
    public BookType getType() {
        return BookType.ACADEMIC;
    }
}
