package tw.edu.ntub.imd.birc.knowledgehub.exception.form;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class MissingFieldException extends ProjectException {
    private final String fieldName;

    public MissingFieldException(String fieldName) {
        super("缺少必要欄位:" + fieldName);
        this.fieldName = fieldName;
    }

    @Override
    public String getErrorCode() {
        return "FormValidation - Invalid";
    }
}
