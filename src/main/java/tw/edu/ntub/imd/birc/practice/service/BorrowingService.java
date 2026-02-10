package tw.edu.ntub.imd.birc.practice.service;

import java.time.LocalDateTime;

public interface BorrowingService {

    LocalDateTime borrowBook(String isbn);

    LocalDateTime returnBook(String isbn);
}
