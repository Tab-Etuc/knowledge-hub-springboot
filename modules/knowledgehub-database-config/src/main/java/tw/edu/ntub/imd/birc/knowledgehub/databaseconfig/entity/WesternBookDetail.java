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
@Table(name = "western_book_detail")
public class WesternBookDetail implements Serializable {

    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "dewey_decimal_code", length = 50, nullable = false)
    private String deweyDecimalCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book;
}
