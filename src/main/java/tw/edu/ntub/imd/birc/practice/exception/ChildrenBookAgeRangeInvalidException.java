package tw.edu.ntub.imd.birc.practice.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class ChildrenBookAgeRangeInvalidException extends ProjectException {
    public ChildrenBookAgeRangeInvalidException() {
        super("兒童書年齡範圍不合法");
    }

    public ChildrenBookAgeRangeInvalidException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ChildrenBook - AgeRangeInvalid";
    }
}
