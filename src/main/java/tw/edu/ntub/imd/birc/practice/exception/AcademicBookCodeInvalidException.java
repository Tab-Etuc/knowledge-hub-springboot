package tw.edu.ntub.imd.birc.practice.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class AcademicBookCodeInvalidException extends ProjectException {
    public AcademicBookCodeInvalidException() {
        super("學術書分類號格式不正確");
    }

    @Override
    public String getErrorCode() {
        return "AcademicBook - CodeInvalid";
    }
}
