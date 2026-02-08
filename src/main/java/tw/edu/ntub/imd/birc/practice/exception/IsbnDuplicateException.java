package tw.edu.ntub.imd.birc.practice.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class IsbnDuplicateException extends ProjectException {
    public IsbnDuplicateException() {
        super("ISBN 已存在");
    }

    @Override
    public String getErrorCode() {
        return "ISBN - Duplicate";
    }
}
