package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.AcademicBookDetailDAO;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.AcademicBookDetail;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.AcademicBookCodeInvalidException;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AcademicBookStrategy extends AbstractBookTypeStrategy {

    private final AcademicBookDetailDAO academicBookDetailDAO;

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
        academicBookDetailDAO.save(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        if (bean.getLcClassMark() != null) {
            validatePattern(bean.getLcClassMark(), LC_CLASS_MARK_PATTERN, new AcademicBookCodeInvalidException());
            academicBookDetailDAO.findById(book.getIsbn())
                    .ifPresent(detail -> detail.setLcClassMark(bean.getLcClassMark()));
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        Optional.ofNullable(updateBean.getLcClassMark()).ifPresent(fullBean::setLcClassMark);
    }

    @Override
    public void populateBean(Book book, BookBean bean) {
        academicBookDetailDAO.findById(book.getIsbn())
                .ifPresent(detail -> bean.setLcClassMark(detail.getLcClassMark()));
    }

    @Override
    public void addResponseFields(BookBean bean, ObjectData data) {
        data.add("lcClassMark", bean.getLcClassMark());
    }

    @Override
    public BookType getType() {
        return BookType.ACADEMIC;
    }
}
