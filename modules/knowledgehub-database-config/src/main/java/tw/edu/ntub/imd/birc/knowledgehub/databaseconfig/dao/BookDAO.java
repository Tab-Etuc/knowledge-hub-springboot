package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;

import javax.annotation.Nonnull;
import java.util.Optional;

@Repository
public interface BookDAO extends BaseDAO<Book, String>, JpaSpecificationExecutor<Book> {

    //   帶細節的單筆查詢，用 LEFT JOIN 一次載入 -> 避免 N+1
    @EntityGraph(attributePaths = {
            "chineseBookDetail",
            "westernBookDetail",
            "academicBookDetail",
            "childrenBookDetail"
    })
    @Nonnull
    Optional<Book> findWithDetailsByIsbn(@Nonnull String isbn);

    
    @EntityGraph(attributePaths = {
            "chineseBookDetail",
            "westernBookDetail",
            "academicBookDetail",
            "childrenBookDetail"
    })
    @Nonnull
    @Override
    Page<Book> findAll(Specification<Book> spec, @Nonnull Pageable pageable);
}
