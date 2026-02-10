package tw.edu.ntub.imd.birc.knowledgehub.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.NotFoundException;
import tw.edu.ntub.imd.birc.knowledgehub.controller.assembler.BookResponseAssembler;
import tw.edu.ntub.imd.birc.knowledgehub.service.BookService;
import tw.edu.ntub.imd.birc.knowledgehub.service.BorrowRecordService;
import tw.edu.ntub.imd.birc.knowledgehub.service.BorrowingService;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BorrowRecordBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.date.LocalDateTimeUtils;
import tw.edu.ntub.imd.birc.knowledgehub.util.http.ResponseEntityBuilder;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BorrowRecordService borrowRecordService;
    private final BorrowingService borrowingService;
    private final BookResponseAssembler bookResponseAssembler;

    public BookController(BookService bookService,
                          BorrowRecordService borrowRecordService,
                          BorrowingService borrowingService,
                          BookResponseAssembler bookResponseAssembler) {
        this.bookService = bookService;
        this.borrowRecordService = borrowRecordService;
        this.borrowingService = borrowingService;
        this.bookResponseAssembler = bookResponseAssembler;
    }

    @PostMapping("")
    public ResponseEntity<String> createBook(
            @Validated(BookBean.Create.class) @RequestBody BookBean bookBean) {
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

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(bookResponseAssembler.toListResponse(bookListBean))
                .build();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<String> getBookDetail(@PathVariable String isbn) {
        BookBean book = bookService.getByIsbn(isbn)
                .orElseThrow(NotFoundException::byIsbn);

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(bookResponseAssembler.toDetailResponse(book))
                .build();
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<String> updateBook(@PathVariable String isbn,
                                             @RequestBody BookBean bookBean) {
        bookService.update(isbn, bookBean);
        return ResponseEntityBuilder.success()
                .message("更新成功")
                .build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntityBuilder.success()
                .message("刪除成功")
                .build();
    }

    @PostMapping("/{isbn}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        LocalDateTime borrowedAt = borrowingService.borrowBook(isbn);
        ObjectData data = new ObjectData().add("borrowedAt", LocalDateTimeUtils.formatIso8601(borrowedAt));
        return ResponseEntityBuilder.success()
                .message("借閱成功")
                .data(data)
                .build();
    }

    @PostMapping("/{isbn}/return")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        LocalDateTime returnedAt = borrowingService.returnBook(isbn);
        ObjectData data = new ObjectData().add("returnedAt", LocalDateTimeUtils.formatIso8601(returnedAt));
        return ResponseEntityBuilder.success()
                .message("歸還成功")
                .data(data)
                .build();
    }

    @GetMapping("/{isbn}/records")
    public ResponseEntity<String> getBorrowRecords(@PathVariable String isbn) {
        bookService.getByIsbn(isbn)
                .orElseThrow(NotFoundException::byIsbn);

        List<BorrowRecordBean> records = borrowRecordService.getRecordsByIsbn(isbn);

        return ResponseEntityBuilder.success()
                .message("查詢成功")
                .data(bookResponseAssembler.toRecordsResponse(records))
                .build();
    }
}
