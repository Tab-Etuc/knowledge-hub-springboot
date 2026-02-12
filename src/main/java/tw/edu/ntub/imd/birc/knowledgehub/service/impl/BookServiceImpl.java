package tw.edu.ntub.imd.birc.knowledgehub.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.BookDAO;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.specification.BookSpecification;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.NotFoundException;
import tw.edu.ntub.imd.birc.knowledgehub.service.BookService;
import tw.edu.ntub.imd.birc.knowledgehub.service.IsbnService;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.strategy.BookStrategyFactory;
import tw.edu.ntub.imd.birc.knowledgehub.service.strategy.BookTypeStrategy;
import tw.edu.ntub.imd.birc.knowledgehub.service.transformer.BookTransformer;
import tw.edu.ntub.birc.common.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl extends BaseServiceImpl<BookBean, Book, String> implements BookService {

    private final BookDAO bookDAO;
    private final BookTransformer bookTransformer;
    private final BookStrategyFactory strategyFactory;
    private final IsbnService isbnService;

    public BookServiceImpl(BookDAO bookDAO,
                           BookTransformer bookTransformer,
                           BookStrategyFactory strategyFactory,
                           IsbnService isbnService) {
        super(bookDAO, bookTransformer);
        this.bookDAO = bookDAO;
        this.bookTransformer = bookTransformer;
        this.strategyFactory = strategyFactory;
        this.isbnService = isbnService;
    }

    @Override
    public BookBean save(BookBean bookBean) {
        bookBean.setIsbn(isbnService.clean(bookBean.getIsbn()));
        isbnService.validateFormat(bookBean.getIsbn());
        isbnService.validateNotDuplicate(bookBean.getIsbn());

        BookTypeStrategy strategy = strategyFactory.getStrategy(bookBean.getType());
        strategy.validate(bookBean);

        String classification = strategy.generateClassification(bookBean);

        Book book = bookTransformer.transferToEntity(bookBean);
        book.setClassification(classification);
        book.setSave(true);

        Book savedBook = bookDAO.save(book);  // 先存 Book
        strategy.createDetail(savedBook, bookBean);  // 再存 Detail（FK 需要 Book 先存在）

        return bookTransformer.transferToBean(savedBook);
    }

    @Override
    public Optional<BookBean> getByIsbn(String isbn) {
        return bookDAO.findById(isbnService.clean(isbn)).map(bookTransformer::transferToBean);
    }

    @Override
    public BookListBean searchBooks(String keyword, BookType type, Boolean available, Pageable pageable) {
        Page<Book> page = bookDAO.findAll(BookSpecification.withFilters(keyword, type, available), pageable);
        List<BookBean> books = CollectionUtils.map(page.getContent(), bookTransformer::transferToListBean);
        return new BookListBean(page.getTotalElements(), books);
    }

    @Override
    public void update(String isbn, BookBean bookBean) {
        isbn = isbnService.clean(isbn);
        Book book = bookDAO.findById(isbn)
                .orElseThrow(NotFoundException::byIsbn);

        Optional.ofNullable(bookBean.getTitle()).ifPresent(book::setTitle);
        Optional.ofNullable(bookBean.getAuthor()).ifPresent(book::setAuthor);
        Optional.ofNullable(bookBean.getPublishedAt()).ifPresent(book::setPublishedAt);

        BookTypeStrategy strategy = strategyFactory.getStrategy(book.getType());
        strategy.updateDetail(book, bookBean);

        BookBean fullBean = bookTransformer.transferToBean(book);
        strategy.mergeUpdatedFields(fullBean, bookBean);
        book.setClassification(strategy.generateClassification(fullBean));

        bookDAO.update(book);
    }

    @Override
    public void delete(String isbn) {
        isbn = isbnService.clean(isbn);
        Book book = bookDAO.findById(isbn)
                .orElseThrow(NotFoundException::byIsbn);
        bookDAO.delete(book);
    }

}
