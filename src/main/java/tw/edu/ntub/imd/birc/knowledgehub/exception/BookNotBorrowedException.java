package tw.edu.ntub.imd.birc.knowledgehub.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class BookNotBorrowedException extends ProjectException {
    public BookNotBorrowedException() {
        super("該書目前在館內");
    }

    @Override
    public String getErrorCode() {
        return "Book - NotBorrowed";
    }
}
