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
@Table(name = "chinese_book_detail")
public class ChineseBookDetail implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "chinese_ddc_code", length = 50, nullable = false)
    private String chineseDdcCode;
}
