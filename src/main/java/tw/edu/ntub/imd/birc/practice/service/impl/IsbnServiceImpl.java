package tw.edu.ntub.imd.birc.practice.service.impl;

import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.BookDAO;
import tw.edu.ntub.imd.birc.practice.exception.IsbnDuplicateException;
import tw.edu.ntub.imd.birc.practice.exception.IsbnInvalidException;
import tw.edu.ntub.imd.birc.practice.service.IsbnService;
import tw.edu.ntub.imd.birc.practice.util.IsbnUtils;

@Service
public class IsbnServiceImpl implements IsbnService {

    private final BookDAO bookDAO;

    public IsbnServiceImpl(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public String clean(String isbn) {
        return IsbnUtils.clean(isbn);
    }

    @Override
    public void validateFormat(String isbn) {
        if (!IsbnUtils.isValid(isbn)) {
            throw new IsbnInvalidException();
        }
    }

    @Override
    public void validateNotDuplicate(String isbn) {
        if (bookDAO.existsByIsbn(isbn)) {
            throw new IsbnDuplicateException();
        }
    }
}
