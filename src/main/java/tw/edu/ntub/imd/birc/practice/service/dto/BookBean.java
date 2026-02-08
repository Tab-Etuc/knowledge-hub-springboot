package tw.edu.ntub.imd.birc.practice.service.dto;

import lombok.Data;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookBean {
    private String isbn;
    private String title;
    private String author;
    private BookType type;
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
