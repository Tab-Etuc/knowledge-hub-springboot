package tw.edu.ntub.imd.birc.knowledgehub.service.impl;

import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.BookDAO;
import tw.edu.ntub.imd.birc.knowledgehub.exception.IsbnDuplicateException;
import tw.edu.ntub.imd.birc.knowledgehub.exception.IsbnInvalidException;
import tw.edu.ntub.imd.birc.knowledgehub.service.IsbnService;
import tw.edu.ntub.imd.birc.knowledgehub.util.IsbnUtils;

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
        if (bookDAO.existsById(isbn)) {
            throw new IsbnDuplicateException();
        }
    }
}
