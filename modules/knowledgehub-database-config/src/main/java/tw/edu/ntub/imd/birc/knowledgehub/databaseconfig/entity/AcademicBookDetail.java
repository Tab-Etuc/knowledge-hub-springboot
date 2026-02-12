package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "academic_book_detail")
public class AcademicBookDetail implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "lc_class_mark", length = 50, nullable = false)
    private String lcClassMark;
}
