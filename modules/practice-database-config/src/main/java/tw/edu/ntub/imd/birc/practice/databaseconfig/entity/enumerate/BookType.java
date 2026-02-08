package tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate;

import lombok.Getter;

public enum BookType {
    CHINESE("CHINESE", "中文書"),
    WESTERN("WESTERN", "西文書"),
    ACADEMIC("ACADEMIC", "學術書"),
    CHILDREN("CHILDREN", "兒童書");

    @Getter
    private final String value;
    @Getter
    private final String typeName;

    BookType(String value, String typeName) {
        this.value = value;
        this.typeName = typeName;
    }

    public static BookType of(String value) {
        for (BookType bookType : BookType.values()) {
            if (bookType.getValue().equalsIgnoreCase(value)) {
                return bookType;
            }
        }
        throw new IllegalArgumentException("不支援的書籍類型: " + value);
    }
}
