package tw.edu.ntub.imd.birc.practice.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class IsbnInvalidException extends ProjectException {
    public IsbnInvalidException() {
        super("ISBN 格式不正確");
    }

    @Override
    public String getErrorCode() {
        return "ISBN - Invalid";
    }
}
