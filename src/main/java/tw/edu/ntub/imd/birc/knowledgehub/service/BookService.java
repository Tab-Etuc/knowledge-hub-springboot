package tw.edu.ntub.imd.birc.knowledgehub.service;

import org.springframework.data.domain.Pageable;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookListBean;

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
}
