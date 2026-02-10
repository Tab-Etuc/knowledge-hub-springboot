package tw.edu.ntub.imd.birc.knowledgehub.dto;

import tw.edu.ntub.imd.birc.knowledgehub.dto.file.directory.Directory;

import java.nio.file.StandardCopyOption;

public interface Copyable {
    void copyTo(Directory newDirectory, StandardCopyOption... options);
}
