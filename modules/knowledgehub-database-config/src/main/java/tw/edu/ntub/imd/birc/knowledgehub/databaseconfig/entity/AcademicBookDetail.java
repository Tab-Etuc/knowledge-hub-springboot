package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString(exclude = {"book"})
@EqualsAndHashCode(exclude = {"book"})
@Entity
@Table(name = "academic_book_detail")
public class AcademicBookDetail implements Serializable {

    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "lc_class_mark", length = 50, nullable = false)
    private String lcClassMark;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book;
}
