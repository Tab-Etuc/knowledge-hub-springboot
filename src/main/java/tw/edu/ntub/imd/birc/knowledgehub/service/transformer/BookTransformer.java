package tw.edu.ntub.imd.birc.knowledgehub.service.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.strategy.BookStrategyFactory;

import javax.annotation.Nonnull;

@Component
@RequiredArgsConstructor
public class BookTransformer implements BeanEntityTransformer<BookBean, Book> {

    private final BookStrategyFactory strategyFactory;

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
        BookBean bean = transferToListBean(book);

        if (book.getType() != null) {
            strategyFactory.getStrategy(book.getType()).populateBean(book, bean);
        }

        return bean;
    }

    // 列表查詢不需要細節
    @Nonnull
    public BookBean transferToListBean(@Nonnull Book book) {
        BookBean bean = new BookBean();
        bean.setIsbn(book.getIsbn());
        bean.setTitle(book.getTitle());
        bean.setAuthor(book.getAuthor());
        bean.setType(book.getType());
        bean.setPublishedAt(book.getPublishedAt());
        bean.setBorrowedAt(book.getBorrowedAt());
        bean.setReturnedAt(book.getReturnedAt());
        bean.setClassification(book.getClassification());
        return bean;
    }
}
