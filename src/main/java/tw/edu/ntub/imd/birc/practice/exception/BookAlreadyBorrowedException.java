package tw.edu.ntub.imd.birc.practice.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class BookAlreadyBorrowedException extends ProjectException {
    public BookAlreadyBorrowedException() {
        super("該書已被借出");
    }

    @Override
    public String getErrorCode() {
        return "Book - AlreadyBorrowed";
    }
}
