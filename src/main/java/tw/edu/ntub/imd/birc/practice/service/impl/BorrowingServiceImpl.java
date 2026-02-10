package tw.edu.ntub.imd.birc.practice.service.impl;

import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.BookDAO;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.BorrowRecordDAO;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.BorrowRecord;
import tw.edu.ntub.imd.birc.practice.exception.BookAlreadyBorrowedException;
import tw.edu.ntub.imd.birc.practice.exception.BookNotBorrowedException;
import tw.edu.ntub.imd.birc.practice.exception.NotFoundException;
import tw.edu.ntub.imd.birc.practice.service.BorrowingService;
import tw.edu.ntub.imd.birc.practice.service.IsbnService;
import tw.edu.ntub.imd.birc.practice.util.date.LocalDateTimeUtils;

import java.time.LocalDateTime;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    private final BookDAO bookDAO;
    private final BorrowRecordDAO borrowRecordDAO;
    private final IsbnService isbnService;

    public BorrowingServiceImpl(BookDAO bookDAO, BorrowRecordDAO borrowRecordDAO, IsbnService isbnService) {
        this.bookDAO = bookDAO;
        this.borrowRecordDAO = borrowRecordDAO;
        this.isbnService = isbnService;
    }

    @Override
    public String borrowBook(String isbn) {
        isbn = isbnService.clean(isbn);
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
        isbn = isbnService.clean(isbn);
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
