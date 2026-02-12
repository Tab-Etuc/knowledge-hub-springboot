package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.ChildrenBookDetailDAO;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.ChildrenBookDetail;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.ChildrenBookAgeRangeInvalidException;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChildrenBookStrategy extends AbstractBookTypeStrategy {

    private final ChildrenBookDetailDAO childrenBookDetailDAO;

    @Override
    public void validate(BookBean bean) {
        validateRequired(bean.getAgeLowerBound(), "ageLowerBound");
        validateRequired(bean.getAgeUpperBound(), "ageUpperBound");
        validateRequired(bean.getTheme(), "theme");
        validateAgeRange(bean.getAgeLowerBound(), bean.getAgeUpperBound());
    }

    @Override
    public String generateClassification(BookBean bean) {
        return "Age " + bean.getAgeLowerBound() + "–" + bean.getAgeUpperBound() + " / " + bean.getTheme();
    }

    @Override
    public void createDetail(Book book, BookBean bean) {
        ChildrenBookDetail detail = new ChildrenBookDetail();
        detail.setIsbn(book.getIsbn());
        detail.setAgeLowerBound(bean.getAgeLowerBound());
        detail.setAgeUpperBound(bean.getAgeUpperBound());
        detail.setTheme(bean.getTheme());
        childrenBookDetailDAO.save(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        childrenBookDetailDAO.findById(book.getIsbn()).ifPresent(detail -> {
            Integer newLower = bean.getAgeLowerBound() != null ? bean.getAgeLowerBound() : detail.getAgeLowerBound();
            Integer newUpper = bean.getAgeUpperBound() != null ? bean.getAgeUpperBound() : detail.getAgeUpperBound();

            if (bean.getAgeLowerBound() != null || bean.getAgeUpperBound() != null) {
                validateAgeRange(newLower, newUpper);
            }

            Optional.ofNullable(bean.getAgeLowerBound()).ifPresent(detail::setAgeLowerBound);
            Optional.ofNullable(bean.getAgeUpperBound()).ifPresent(detail::setAgeUpperBound);
            Optional.ofNullable(bean.getTheme()).ifPresent(detail::setTheme);
        });
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        Optional.ofNullable(updateBean.getAgeLowerBound()).ifPresent(fullBean::setAgeLowerBound);
        Optional.ofNullable(updateBean.getAgeUpperBound()).ifPresent(fullBean::setAgeUpperBound);
        Optional.ofNullable(updateBean.getTheme()).ifPresent(fullBean::setTheme);
    }

    @Override
    public void populateBean(Book book, BookBean bean) {
        childrenBookDetailDAO.findById(book.getIsbn()).ifPresent(detail -> {
            bean.setAgeLowerBound(detail.getAgeLowerBound());
            bean.setAgeUpperBound(detail.getAgeUpperBound());
            bean.setTheme(detail.getTheme());
        });
    }

    @Override
    public void addResponseFields(BookBean bean, ObjectData data) {
        data.add("ageLowerBound", bean.getAgeLowerBound());
        data.add("ageUpperBound", bean.getAgeUpperBound());
        data.add("theme", bean.getTheme());
    }

    @Override
    public BookType getType() {
        return BookType.CHILDREN;
    }

    private void validateAgeRange(Integer lower, Integer upper) {
        if (lower <= 0 || lower >= 12 || upper <= 0 || upper >= 12 || lower >= upper) {
            throw new ChildrenBookAgeRangeInvalidException();
        }

    }
}
