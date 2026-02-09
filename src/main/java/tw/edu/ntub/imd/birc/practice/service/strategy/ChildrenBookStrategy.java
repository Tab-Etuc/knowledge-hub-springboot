package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.ChildrenBookDetail;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.ChildrenBookAgeRangeInvalidException;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

@Component
public class ChildrenBookStrategy implements BookTypeStrategy {

    @Override
    public void validate(BookBean bean) {
        if (bean.getAgeLowerBound() == null) {
            throw new MissingFieldException("ageLowerBound");
        }
        if (bean.getAgeUpperBound() == null) {
            throw new MissingFieldException("ageUpperBound");
        }
        if (bean.getTheme() == null || bean.getTheme().isBlank()) {
            throw new MissingFieldException("theme");
        }
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
        book.setChildrenBookDetail(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        ChildrenBookDetail detail = book.getChildrenBookDetail();
        if (detail != null) {
            Integer newLower = bean.getAgeLowerBound() != null ? bean.getAgeLowerBound() : detail.getAgeLowerBound();
            Integer newUpper = bean.getAgeUpperBound() != null ? bean.getAgeUpperBound() : detail.getAgeUpperBound();

            if (bean.getAgeLowerBound() != null || bean.getAgeUpperBound() != null) {
                validateAgeRange(newLower, newUpper);
            }

            if (bean.getAgeLowerBound() != null) {
                detail.setAgeLowerBound(bean.getAgeLowerBound());
            }
            if (bean.getAgeUpperBound() != null) {
                detail.setAgeUpperBound(bean.getAgeUpperBound());
            }
            if (bean.getTheme() != null) {
                detail.setTheme(bean.getTheme());
            }
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        if (updateBean.getAgeLowerBound() != null) {
            fullBean.setAgeLowerBound(updateBean.getAgeLowerBound());
        }
        if (updateBean.getAgeUpperBound() != null) {
            fullBean.setAgeUpperBound(updateBean.getAgeUpperBound());
        }
        if (updateBean.getTheme() != null) {
            fullBean.setTheme(updateBean.getTheme());
        }
    }

    @Override
    public BookType getType() {
        return BookType.CHILDREN;
    }

    private void validateAgeRange(Integer lower, Integer upper) {
        if (lower <= 0 || lower >= 12 || upper <= 0 || upper >= 12) {
            throw new ChildrenBookAgeRangeInvalidException();
        }
        if (lower >= upper) {
            throw new ChildrenBookAgeRangeInvalidException();
        }
    }
}
