package tw.edu.ntub.imd.birc.knowledgehub.exception.form;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class InvalidRequestFormatException extends ProjectException {
    public InvalidRequestFormatException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "FormValidation - Invalid";
    }
}
