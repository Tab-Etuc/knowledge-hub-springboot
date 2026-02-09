package tw.edu.ntub.imd.birc.practice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.*;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.specification.BookSpecification;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.*;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.*;
import tw.edu.ntub.imd.birc.practice.service.BookService;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.practice.service.strategy.BookStrategyFactory;
import tw.edu.ntub.imd.birc.practice.service.strategy.BookTypeStrategy;
import tw.edu.ntub.imd.birc.practice.service.transformer.BookTransformer;
import tw.edu.ntub.birc.common.util.CollectionUtils;

import tw.edu.ntub.imd.birc.practice.util.IsbnUtils;
import tw.edu.ntub.imd.birc.practice.util.date.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl extends BaseServiceImpl<BookBean, Book, String> implements BookService {

    private final BookDAO bookDAO;
    private final BorrowRecordDAO borrowRecordDAO;
    private final BookTransformer bookTransformer;
    private final BookStrategyFactory strategyFactory;

    public BookServiceImpl(BookDAO bookDAO,
                           BorrowRecordDAO borrowRecordDAO,
                           BookTransformer bookTransformer,
                           BookStrategyFactory strategyFactory) {
        super(bookDAO, bookTransformer);
        this.bookDAO = bookDAO;
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookTransformer = bookTransformer;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public BookBean save(BookBean bookBean) {
        bookBean.setIsbn(IsbnUtils.clean(bookBean.getIsbn()));
        if (!IsbnUtils.isValid(bookBean.getIsbn())) {
            throw new IsbnInvalidException();
        }

        if (bookDAO.existsByIsbn(bookBean.getIsbn())) {
            throw new IsbnDuplicateException();
        }

        BookTypeStrategy strategy = strategyFactory.getStrategy(bookBean.getType());
        strategy.validate(bookBean);

        String classification = strategy.generateClassification(bookBean);

        Book book = bookTransformer.transferToEntity(bookBean);
        book.setClassification(classification);
        book.setSave(true);

        strategy.createDetail(book, bookBean);

        Book savedBook = bookDAO.save(book);
        return bookTransformer.transferToBean(savedBook);
    }

    @Override
    public Optional<BookBean> getByIsbn(String isbn) {
        return bookDAO.findByIsbn(IsbnUtils.clean(isbn)).map(bookTransformer::transferToBean);
    }

    @Override
    public BookListBean searchBooks(String keyword, BookType type, Boolean available, Pageable pageable) {
        Page<Book> page = bookDAO.findAll(BookSpecification.withFilters(keyword, type, available), pageable);
        List<BookBean> books = CollectionUtils.map(page.getContent(), bookTransformer::transferToBean);
        return new BookListBean(page.getTotalElements(), books);
    }

    @Override
    public void update(String isbn, BookBean bookBean) {
        isbn = IsbnUtils.clean(isbn);
        Book book = bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        if (bookBean.getTitle() != null) {
            book.setTitle(bookBean.getTitle());
        }
        if (bookBean.getAuthor() != null) {
            book.setAuthor(bookBean.getAuthor());
        }
        if (bookBean.getPublishedAt() != null) {
            book.setPublishedAt(bookBean.getPublishedAt());
        }

        BookTypeStrategy strategy = strategyFactory.getStrategy(book.getType());
        strategy.updateDetail(book, bookBean);

        BookBean fullBean = bookTransformer.transferToBean(book);
        strategy.mergeUpdatedFields(fullBean, bookBean);
        book.setClassification(strategy.generateClassification(fullBean));

        bookDAO.update(book);
    }

    
    @Override
    public void delete(String isbn) {
        isbn = IsbnUtils.clean(isbn);
        if (!bookDAO.existsByIsbn(isbn)) {
            throw new NotFoundException("找不到該 ISBN");
        }
        bookDAO.deleteById(isbn);
    }

    @Override
    public String borrowBook(String isbn) {
        isbn = IsbnUtils.clean(isbn);
        Book book = bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        if (book.getBorrowedAt() != null) {
            throw new BookAlreadyBorrowedException();
        }

        LocalDateTime now = LocalDateTime.now(LocalDateTimeUtils.TAIPEI_ZONE);
        book.setBorrowedAt(now);
        book.setReturnedAt(null);

        BorrowRecord record = new BorrowRecord();
        record.setIsbn(isbn);
        record.setBorrowedAt(now);
        record.setSave(true);
        borrowRecordDAO.save(record);

        bookDAO.update(book);

        return LocalDateTimeUtils.formatIso8601(now);
    }

    @Override
    public String returnBook(String isbn) {
        isbn = IsbnUtils.clean(isbn);
        Book book = bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        if (book.getBorrowedAt() == null) {
            throw new BookNotBorrowedException();
        }

        LocalDateTime now = LocalDateTime.now(LocalDateTimeUtils.TAIPEI_ZONE);

        // 直接查詢該 ISBN 尚未歸還的最新借閱紀錄
        borrowRecordDAO.findFirstByIsbnAndReturnedAtIsNullOrderByBorrowedAtDesc(isbn)
                .ifPresent(record -> {
                    record.setReturnedAt(now);
                    borrowRecordDAO.update(record);
                });

        book.setBorrowedAt(null);
        book.setReturnedAt(now);
        bookDAO.update(book);

        return LocalDateTimeUtils.formatIso8601(now);
    }
}
