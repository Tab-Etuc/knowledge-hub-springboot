package tw.edu.ntub.imd.birc.practice.databaseconfig.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString(exclude = {"book"})
@EqualsAndHashCode(exclude = {"book"})
@Entity
@Table(name = "children_book_detail")
public class ChildrenBookDetail implements Serializable {

    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "age_lower_bound", nullable = false)
    private Integer ageLowerBound;

    @Column(name = "age_upper_bound", nullable = false)
    private Integer ageUpperBound;

    @Column(name = "theme", length = 100, nullable = false)
    private String theme;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book;
}
