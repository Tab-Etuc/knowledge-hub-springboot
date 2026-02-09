package tw.edu.ntub.imd.birc.practice.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.exception.NotFoundException;
import tw.edu.ntub.imd.birc.practice.service.BookService;
import tw.edu.ntub.imd.birc.practice.service.BorrowRecordService;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BorrowRecordBean;
import tw.edu.ntub.imd.birc.practice.util.http.ResponseEntityBuilder;
import tw.edu.ntub.imd.birc.practice.util.json.array.ArrayData;
import tw.edu.ntub.imd.birc.practice.util.json.object.ObjectData;

import tw.edu.ntub.imd.birc.practice.util.IsbnUtils;
import tw.edu.ntub.imd.birc.practice.util.date.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BorrowRecordService borrowRecordService;

    public BookController(BookService bookService, BorrowRecordService borrowRecordService) {
        this.bookService = bookService;
        this.borrowRecordService = borrowRecordService;
    }

    @PostMapping("")
    public ResponseEntity<String> createBook(@RequestBody BookBean bookBean) {
        bookService.save(bookBean);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResponseEntityBuilder.success()
                        .message("新增成功")
                        .buildJSONString());
    }

    @GetMapping("")
    public ResponseEntity<String> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BookType type,
            @RequestParam(required = false) Boolean available,
            Pageable pageable) {

        BookListBean bookListBean = bookService.searchBooks(keyword, type, available, pageable);

        ArrayData booksArray = new ArrayData();
        for (BookBean book : bookListBean.getBooks()) {
            booksArray.addObject()
                    .add("isbn", book.getIsbn())
                    .add("title", book.getTitle())
                    .add("type", book.getType().name())
                    .add("classification", book.getClassification())
                    .add("isAvailable", book.getBorrowedAt() == null);
        }

        ObjectData data = new ObjectData()
                .add("total_count", bookListBean.getTotalCount())
                .add("books", booksArray);

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(data)
                .build();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<String> getBookDetail(@PathVariable String isbn) {
        isbn = IsbnUtils.clean(isbn);
        BookBean book = bookService.getByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        ObjectData data = new ObjectData()
                .add("isbn", book.getIsbn())
                .add("title", book.getTitle())
                .add("author", book.getAuthor())
                .add("type", book.getType().name())
                .add("publishedAt", book.getPublishedAt());

        addDateTimeField(data, "borrowedAt", book.getBorrowedAt());
        addDateTimeField(data, "returnedAt", book.getReturnedAt());

        data.add("classification", book.getClassification());

        addTypeSpecificFields(data, book);

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(data)
                .build();
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<String> updateBook(@PathVariable String isbn,
                                             @RequestBody BookBean bookBean) {
        isbn = IsbnUtils.clean(isbn);
        bookService.update(isbn, bookBean);
        return ResponseEntityBuilder.success()
                .message("更新成功")
                .build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        isbn = IsbnUtils.clean(isbn);
        bookService.delete(isbn);
        return ResponseEntityBuilder.success()
                .message("刪除成功")
                .build();
    }

    @PostMapping("/{isbn}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        isbn = IsbnUtils.clean(isbn);
        String borrowedAt = bookService.borrowBook(isbn);
        ObjectData data = new ObjectData().add("borrowedAt", borrowedAt);
        return ResponseEntityBuilder.success()
                .message("借閱成功")
                .data(data)
                .build();
    }

    @PostMapping("/{isbn}/return")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        isbn = IsbnUtils.clean(isbn);
        String returnedAt = bookService.returnBook(isbn);
        ObjectData data = new ObjectData().add("returnedAt", returnedAt);
        return ResponseEntityBuilder.success()
                .message("歸還成功")
                .data(data)
                .build();
    }

    @GetMapping("/{isbn}/records")
    public ResponseEntity<String> getBorrowRecords(@PathVariable String isbn) {
        isbn = IsbnUtils.clean(isbn);
        bookService.getByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("找不到該 ISBN"));

        List<BorrowRecordBean> records = borrowRecordService.getRecordsByIsbn(isbn);

        ArrayData recordsArray = new ArrayData();
        for (BorrowRecordBean record : records) {
            ObjectData recordData = recordsArray.addObject();
            addDateTimeField(recordData, "borrowedAt", record.getBorrowedAt());
            addDateTimeField(recordData, "returnedAt", record.getReturnedAt());
        }

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(recordsArray)
                .build();
    }


    private void addDateTimeField(ObjectData data, String key, LocalDateTime dateTime) {
        String formatted = LocalDateTimeUtils.formatIso8601(dateTime);
        if (formatted != null) {
            data.add(key, formatted);
        } else {
            data.addNull(key);
        }
    }

    private void addTypeSpecificFields(ObjectData data, BookBean book) {
        switch (book.getType()) {
            case CHINESE:
                data.add("chineseDdcCode", book.getChineseDdcCode());
                break;
            case WESTERN:
                data.add("deweyDecimalCode", book.getDeweyDecimalCode());
                break;
            case ACADEMIC:
                data.add("lcClassMark", book.getLcClassMark());
                break;
            case CHILDREN:
                data.add("ageLowerBound", book.getAgeLowerBound());
                data.add("ageUpperBound", book.getAgeUpperBound());
                data.add("theme", book.getTheme());
                break;
        }
    }
}
