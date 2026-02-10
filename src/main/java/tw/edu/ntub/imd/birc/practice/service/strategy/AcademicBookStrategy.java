package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.AcademicBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.AcademicBookCodeInvalidException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class AcademicBookStrategy extends AbstractBookTypeStrategy {

    private static final String LC_CLASS_MARK_PATTERN = "^[A-Z]{1,2}\\d.*$";

    @Override
    public void validate(BookBean bean) {
        validateRequired(bean.getLcClassMark(), "lcClassMark");
        validatePattern(bean.getLcClassMark(), LC_CLASS_MARK_PATTERN, new AcademicBookCodeInvalidException());
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
            validatePattern(bean.getLcClassMark(), LC_CLASS_MARK_PATTERN, new AcademicBookCodeInvalidException());
            safeUpdateDetail(book, Book::getAcademicBookDetail,
                    detail -> detail.setLcClassMark(bean.getLcClassMark()));
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
