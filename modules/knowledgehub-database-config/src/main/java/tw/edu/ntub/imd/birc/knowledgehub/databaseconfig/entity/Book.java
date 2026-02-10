package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.converter.BookTypeConverter;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"chineseBookDetail", "westernBookDetail", "academicBookDetail", "childrenBookDetail"})
@EqualsAndHashCode(exclude = {"chineseBookDetail", "westernBookDetail", "academicBookDetail", "childrenBookDetail"})
@Entity
@Table(name = "book")
@EntityListeners(AuditingEntityListener.class)
public class Book implements Serializable, Persistable<String> {

    @Transient
    private Boolean save;

    @Id
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "type", nullable = false)
    @Convert(converter = BookTypeConverter.class)
    private BookType type;

    @Column(name = "published_at", nullable = false)
    private LocalDate publishedAt;

    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "classification", length = 100)
    private String classification;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ChineseBookDetail chineseBookDetail;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private WesternBookDetail westernBookDetail;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AcademicBookDetail academicBookDetail;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ChildrenBookDetail childrenBookDetail;

    @Override
    public String getId() {
        return isbn;
    }
}
