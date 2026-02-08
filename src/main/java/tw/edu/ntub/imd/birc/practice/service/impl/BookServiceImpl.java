package tw.edu.ntub.imd.birc.practice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.*;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.specification.BookSpecification;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.*;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.*;
import tw.edu.ntub.imd.birc.practice.exception.form.MissingFieldException;
import tw.edu.ntub.imd.birc.practice.service.BookService;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.practice.service.transformer.BookTransformer;
import tw.edu.ntub.birc.common.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl extends BaseServiceImpl<BookBean, Book, String> implements BookService {

    private static final ZoneId TAIPEI_ZONE = ZoneId.of("Asia/Taipei");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final BookDAO bookDAO;
    private final BorrowRecordDAO borrowRecordDAO;
    private final BookTransformer bookTransformer;

    public BookServiceImpl(BookDAO bookDAO,
                           BorrowRecordDAO borrowRecordDAO,
                           BookTransformer bookTransformer) {
        super(bookDAO, bookTransformer);
        this.bookDAO = bookDAO;
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookTransformer = bookTransformer;
    }

    @Override
    public BookBean save(BookBean bookBean) {
        validateCommonFields(bookBean);

        if (!isValidIsbn13(bookBean.getIsbn())) {
            throw new IsbnInvalidException();
        }

        if (bookDAO.existsByIsbn(bookBean.getIsbn())) {
            throw new IsbnDuplicateException();
        }

        validateTypeSpecificFields(bookBean);

        String classification = generateClassification(bookBean);

        Book book = bookTransformer.transferToEntity(bookBean);
        book.setClassification(classification);
        book.setSave(true);

        createBookDetail(book, bookBean);

        Book savedBook = bookDAO.save(book);
        return bookTransformer.transferToBean(savedBook);
    }

    @Override
    public Optional<BookBean> getByIsbn(String isbn) {
        return bookDAO.findByIsbn(isbn).map(bookTransformer::transferToBean);
    }

    @Override
    public BookListBean searchBooks(String keyword, BookType type, Boolean available, Pageable pageable) {
        Page<Book> page = bookDAO.findAll(BookSpecification.withFilters(keyword, type, available), pageable);
        List<BookBean> books = CollectionUtils.map(page.getContent(), bookTransformer::transferToBean);
        return new BookListBean(page.getTotalElements(), books);
    }

    @Override
    public void update(String isbn, BookBean bookBean) {
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

        updateTypeSpecificFields(book, bookBean);

        BookBean fullBean = bookTransformer.transferToBean(book);
        mergeUpdatedFields(fullBean, bookBean, book.getType());
        book.setClassification(generateClassification(fullBean));

        bookDAO.update(book);
    }

    
    @Override
    public void delete(String isbn) {
        if (!bookDAO.existsByIsbn(isbn)) {
            throw new NotFoundException("找不到該 ISBN");
        }
        bookDAO.deleteById(isbn);
    }

    @Override
    public String borrowBook(String isbn) {
        Book book = bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        if (book.getBorrowedAt() != null) {
            throw new BookAlreadyBorrowedException();
        }

        LocalDateTime now = LocalDateTime.now(TAIPEI_ZONE);
        book.setBorrowedAt(now);
        book.setReturnedAt(null);

        BorrowRecord record = new BorrowRecord();
        record.setIsbn(isbn);
        record.setBorrowedAt(now);
        record.setSave(true);
        borrowRecordDAO.save(record);

        bookDAO.update(book);

        return now.atZone(TAIPEI_ZONE).format(ISO_FORMATTER);
    }

    @Override
    public String returnBook(String isbn) {
        Book book = bookDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        if (book.getBorrowedAt() == null) {
            throw new BookNotBorrowedException();
        }

        LocalDateTime now = LocalDateTime.now(TAIPEI_ZONE);

        // 直接查詢該 ISBN 尚未歸還的最新借閱紀錄
        borrowRecordDAO.findFirstByIsbnAndReturnedAtIsNullOrderByBorrowedAtDesc(isbn)
                .ifPresent(record -> {
                    record.setReturnedAt(now);
                    borrowRecordDAO.update(record);
                });

        book.setBorrowedAt(null);
        book.setReturnedAt(now);
        bookDAO.update(book);

        return now.atZone(TAIPEI_ZONE).format(ISO_FORMATTER);
    }


    private void validateCommonFields(BookBean bean) {
        if (bean.getIsbn() == null || bean.getIsbn().isBlank()) {
            throw new MissingFieldException("isbn");
        }
        if (bean.getTitle() == null || bean.getTitle().isBlank()) {
            throw new MissingFieldException("title");
        }
        if (bean.getAuthor() == null || bean.getAuthor().isBlank()) {
            throw new MissingFieldException("author");
        }
        if (bean.getType() == null) {
            throw new MissingFieldException("type");
        }
        if (bean.getPublishedAt() == null) {
            throw new MissingFieldException("publishedAt");
        }
    }

    private void validateTypeSpecificFields(BookBean bean) {
        switch (bean.getType()) {
            case CHINESE:
                if (bean.getChineseDdcCode() == null || bean.getChineseDdcCode().isBlank()) {
                    throw new MissingFieldException("chineseDdcCode");
                }
                validateChineseDdcCode(bean.getChineseDdcCode());
                break;
            case WESTERN:
                if (bean.getDeweyDecimalCode() == null || bean.getDeweyDecimalCode().isBlank()) {
                    throw new MissingFieldException("deweyDecimalCode");
                }
                validateDeweyDecimalCode(bean.getDeweyDecimalCode());
                break;
            case ACADEMIC:
                if (bean.getLcClassMark() == null || bean.getLcClassMark().isBlank()) {
                    throw new MissingFieldException("lcClassMark");
                }
                validateLcClassMark(bean.getLcClassMark());
                break;
            case CHILDREN:
                if (bean.getAgeLowerBound() == null) {
                    throw new MissingFieldException("ageLowerBound");
                }
                if (bean.getAgeUpperBound() == null) {
                    throw new MissingFieldException("ageUpperBound");
                }
                if (bean.getTheme() == null || bean.getTheme().isBlank()) {
                    throw new MissingFieldException("theme");
                }
                validateChildrenAgeRange(bean.getAgeLowerBound(), bean.getAgeUpperBound());
                break;
        }
    }

    private void validateChineseDdcCode(String code) {
        if (!code.matches("^[A-Z]\\d.*$")) {
            throw new ChineseBookCodeInvalidException();
        }
    }

    private void validateDeweyDecimalCode(String code) {
        if (!code.matches("^\\d{3}(\\.\\d+)?$")) {
            throw new WesternBookCodeInvalidException();
        }
    }

    private void validateLcClassMark(String code) {
        if (!code.matches("^[A-Z]{1,2}\\d.*$")) {
            throw new AcademicBookCodeInvalidException();
        }
    }

    private void validateChildrenAgeRange(Integer lower, Integer upper) {
        if (lower <= 0 || lower >= 12 || upper <= 0 || upper >= 12) {
            throw new ChildrenBookAgeRangeInvalidException();
        }
        if (lower >= upper) {
            throw new ChildrenBookAgeRangeInvalidException();
        }
    }


    private boolean isValidIsbn13(String isbn) {
        if (isbn == null) return false;
        String cleanIsbn = isbn.replaceAll("-", "").replaceAll(" ", "");
        if (cleanIsbn.length() != 13 || !cleanIsbn.matches("\\d{13}")) return false;

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(cleanIsbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(cleanIsbn.charAt(12));
    }


    private String generateClassification(BookBean bean) {
        switch (bean.getType()) {
            case CHINESE:
                return bean.getChineseDdcCode();
            case WESTERN:
                return bean.getDeweyDecimalCode();
            case ACADEMIC:
                return bean.getLcClassMark();
            case CHILDREN:
                return "Age " + bean.getAgeLowerBound() + "–" + bean.getAgeUpperBound() + " / " + bean.getTheme();
            default:
                return "";
        }
    }


    private void createBookDetail(Book book, BookBean bean) {
        switch (bean.getType()) {
            case CHINESE:
                ChineseBookDetail chineseDetail = new ChineseBookDetail();
                chineseDetail.setIsbn(book.getIsbn());
                chineseDetail.setChineseDdcCode(bean.getChineseDdcCode());
                book.setChineseBookDetail(chineseDetail);
                break;
            case WESTERN:
                WesternBookDetail westernDetail = new WesternBookDetail();
                westernDetail.setIsbn(book.getIsbn());
                westernDetail.setDeweyDecimalCode(bean.getDeweyDecimalCode());
                book.setWesternBookDetail(westernDetail);
                break;
            case ACADEMIC:
                AcademicBookDetail academicDetail = new AcademicBookDetail();
                academicDetail.setIsbn(book.getIsbn());
                academicDetail.setLcClassMark(bean.getLcClassMark());
                book.setAcademicBookDetail(academicDetail);
                break;
            case CHILDREN:
                ChildrenBookDetail childrenDetail = new ChildrenBookDetail();
                childrenDetail.setIsbn(book.getIsbn());
                childrenDetail.setAgeLowerBound(bean.getAgeLowerBound());
                childrenDetail.setAgeUpperBound(bean.getAgeUpperBound());
                childrenDetail.setTheme(bean.getTheme());
                book.setChildrenBookDetail(childrenDetail);
                break;
        }
    }


    private void updateTypeSpecificFields(Book book, BookBean bean) {
        switch (book.getType()) {
            case CHINESE:
                if (bean.getChineseDdcCode() != null) {
                    validateChineseDdcCode(bean.getChineseDdcCode());
                    if (book.getChineseBookDetail() != null) {
                        book.getChineseBookDetail().setChineseDdcCode(bean.getChineseDdcCode());
                    }
                }
                break;
            case WESTERN:
                if (bean.getDeweyDecimalCode() != null) {
                    validateDeweyDecimalCode(bean.getDeweyDecimalCode());
                    if (book.getWesternBookDetail() != null) {
                        book.getWesternBookDetail().setDeweyDecimalCode(bean.getDeweyDecimalCode());
                    }
                }
                break;
            case ACADEMIC:
                if (bean.getLcClassMark() != null) {
                    validateLcClassMark(bean.getLcClassMark());
                    if (book.getAcademicBookDetail() != null) {
                        book.getAcademicBookDetail().setLcClassMark(bean.getLcClassMark());
                    }
                }
                break;
            case CHILDREN:
                ChildrenBookDetail detail = book.getChildrenBookDetail();
                if (detail != null) {
                    Integer newLower = bean.getAgeLowerBound() != null ? bean.getAgeLowerBound() : detail.getAgeLowerBound();
                    Integer newUpper = bean.getAgeUpperBound() != null ? bean.getAgeUpperBound() : detail.getAgeUpperBound();

                    if (bean.getAgeLowerBound() != null || bean.getAgeUpperBound() != null) {
                        validateChildrenAgeRange(newLower, newUpper);
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
                break;
        }
    }

    private void mergeUpdatedFields(BookBean fullBean, BookBean updateBean, BookType type) {
        switch (type) {
            case CHINESE:
                if (updateBean.getChineseDdcCode() != null) {
                    fullBean.setChineseDdcCode(updateBean.getChineseDdcCode());
                }
                break;
            case WESTERN:
                if (updateBean.getDeweyDecimalCode() != null) {
                    fullBean.setDeweyDecimalCode(updateBean.getDeweyDecimalCode());
                }
                break;
            case ACADEMIC:
                if (updateBean.getLcClassMark() != null) {
                    fullBean.setLcClassMark(updateBean.getLcClassMark());
                }
                break;
            case CHILDREN:
                if (updateBean.getAgeLowerBound() != null) {
                    fullBean.setAgeLowerBound(updateBean.getAgeLowerBound());
                }
                if (updateBean.getAgeUpperBound() != null) {
                    fullBean.setAgeUpperBound(updateBean.getAgeUpperBound());
                }
                if (updateBean.getTheme() != null) {
                    fullBean.setTheme(updateBean.getTheme());
                }
                break;
        }
    }
}
