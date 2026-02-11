package tw.edu.ntub.imd.birc.knowledgehub.service;

import java.time.LocalDateTime;

public interface BorrowingService {

    LocalDateTime saveBorrowRecord(String isbn);

    LocalDateTime saveReturnRecord(String isbn);
}
