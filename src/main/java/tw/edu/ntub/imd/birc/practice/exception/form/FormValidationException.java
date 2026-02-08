package tw.edu.ntub.imd.birc.practice.exception.form;

import tw.edu.ntub.birc.common.exception.ProjectException;

public class FormValidationException extends ProjectException {
    public FormValidationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "FormValidation - Invalid";
    }
}
