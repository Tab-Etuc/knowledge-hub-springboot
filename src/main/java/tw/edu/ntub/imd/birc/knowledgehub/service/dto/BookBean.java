package tw.edu.ntub.imd.birc.knowledgehub.service.dto;

import lombok.Data;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookBean {
    public interface Create {}  // 驗證群組

    @NotBlank(message = "缺少必要欄位: isbn", groups = Create.class)
    private String isbn;

    @NotBlank(message = "缺少必要欄位: title", groups = Create.class)
    private String title;

    @NotBlank(message = "缺少必要欄位: author", groups = Create.class)
    private String author;

    @NotNull(message = "缺少必要欄位: type", groups = Create.class)
    private BookType type;

    @NotNull(message = "缺少必要欄位: publishedAt", groups = Create.class)
    private LocalDate publishedAt;

    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private String classification;
    private String chineseDdcCode;
    private String deweyDecimalCode;
    private String lcClassMark;
    private Integer ageLowerBound;
    private Integer ageUpperBound;
    private String theme;
}
