package tw.edu.ntub.imd.birc.knowledgehub.service;

public interface IsbnService {

    String clean(String isbn);

    void validateFormat(String isbn);

    void validateNotDuplicate(String isbn);
}
