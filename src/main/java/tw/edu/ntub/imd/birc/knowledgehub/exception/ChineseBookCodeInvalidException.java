package tw.edu.ntub.imd.birc.knowledgehub.exception;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class ChineseBookCodeInvalidException extends ProjectException {
    public ChineseBookCodeInvalidException() {
        super("中文書分類號格式不正確");
    }

    @Override
    public String getErrorCode() {
        return "ChineseBook - CodeInvalid";
    }
}
