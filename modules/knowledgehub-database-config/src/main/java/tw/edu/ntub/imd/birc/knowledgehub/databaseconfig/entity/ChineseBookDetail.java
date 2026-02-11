package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString(exclude = {"book"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "chinese_book_detail")
public class ChineseBookDetail implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "chinese_ddc_code", length = 50, nullable = false)
    private String chineseDdcCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book;
}
