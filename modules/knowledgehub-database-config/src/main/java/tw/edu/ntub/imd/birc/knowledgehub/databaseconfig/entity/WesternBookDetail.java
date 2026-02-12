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
@Table(name = "western_book_detail")
public class WesternBookDetail implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "dewey_decimal_code", length = 50, nullable = false)
    private String deweyDecimalCode;
}
