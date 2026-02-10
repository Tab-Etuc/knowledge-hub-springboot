package tw.edu.ntub.imd.birc.knowledgehub.service;

import java.time.LocalDateTime;

public interface BorrowingService {

    LocalDateTime borrowBook(String isbn);

    LocalDateTime returnBook(String isbn);
}
