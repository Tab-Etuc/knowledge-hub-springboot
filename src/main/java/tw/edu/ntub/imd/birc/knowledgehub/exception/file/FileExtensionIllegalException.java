package tw.edu.ntub.imd.birc.knowledgehub.exception.file;

public class FileExtensionIllegalException extends FileException {
    public FileExtensionIllegalException(String message) {
        super(message);
    }

    @Override
    public String getReason() {
        return "FileExtensionIllegal";
    }
}
