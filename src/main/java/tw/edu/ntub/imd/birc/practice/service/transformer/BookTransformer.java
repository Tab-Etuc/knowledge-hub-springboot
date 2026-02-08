package tw.edu.ntub.imd.birc.practice.service.transformer;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

import javax.annotation.Nonnull;

@Component
public class BookTransformer implements BeanEntityTransformer<BookBean, Book> {

    @Nonnull
    @Override
    public Book transferToEntity(@Nonnull BookBean bookBean) {
        Book book = new Book();
        book.setIsbn(bookBean.getIsbn());
        book.setTitle(bookBean.getTitle());
        book.setAuthor(bookBean.getAuthor());
        book.setType(bookBean.getType());
        book.setPublishedAt(bookBean.getPublishedAt());
        book.setBorrowedAt(bookBean.getBorrowedAt());
        book.setReturnedAt(bookBean.getReturnedAt());
        book.setClassification(bookBean.getClassification());
        return book;
    }

    @Nonnull
    @Override
    public BookBean transferToBean(@Nonnull Book book) {
        BookBean bean = new BookBean();
        bean.setIsbn(book.getIsbn());
        bean.setTitle(book.getTitle());
        bean.setAuthor(book.getAuthor());
        bean.setType(book.getType());
        bean.setPublishedAt(book.getPublishedAt());
        bean.setBorrowedAt(book.getBorrowedAt());
        bean.setReturnedAt(book.getReturnedAt());
        bean.setClassification(book.getClassification());

        if (book.getType() != null) {
            switch (book.getType()) {
                case CHINESE:
                    if (book.getChineseBookDetail() != null) {
                        bean.setChineseDdcCode(book.getChineseBookDetail().getChineseDdcCode());
                    }
                    break;
                case WESTERN:
                    if (book.getWesternBookDetail() != null) {
                        bean.setDeweyDecimalCode(book.getWesternBookDetail().getDeweyDecimalCode());
                    }
                    break;
                case ACADEMIC:
                    if (book.getAcademicBookDetail() != null) {
                        bean.setLcClassMark(book.getAcademicBookDetail().getLcClassMark());
                    }
                    break;
                case CHILDREN:
                    if (book.getChildrenBookDetail() != null) {
                        bean.setAgeLowerBound(book.getChildrenBookDetail().getAgeLowerBound());
                        bean.setAgeUpperBound(book.getChildrenBookDetail().getAgeUpperBound());
                        bean.setTheme(book.getChildrenBookDetail().getTheme());
                    }
                    break;
            }
        }

        return bean;
    }
}
