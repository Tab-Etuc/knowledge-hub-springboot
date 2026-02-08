package tw.edu.ntub.imd.birc.practice.service;

import org.springframework.data.domain.Pageable;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BookListBean;

import java.util.Optional;

public interface BookService extends BaseService<BookBean, String> {


    @Override
    BookBean save(BookBean bookBean);

    Optional<BookBean> getByIsbn(String isbn);

    BookListBean searchBooks(String keyword, BookType type, Boolean available, Pageable pageable);

    @Override
    void update(String isbn, BookBean bookBean);

    @Override
    void delete(String isbn);

    String borrowBook(String isbn);

    String returnBook(String isbn);
}
