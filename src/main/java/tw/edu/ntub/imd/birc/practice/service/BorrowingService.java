package tw.edu.ntub.imd.birc.practice.service;

public interface BorrowingService {

    String borrowBook(String isbn);

    String returnBook(String isbn);
}
