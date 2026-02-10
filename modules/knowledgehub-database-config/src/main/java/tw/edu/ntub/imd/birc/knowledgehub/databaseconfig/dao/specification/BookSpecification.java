package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.specification;

import org.springframework.data.jpa.domain.Specification;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> withFilters(String keyword, BookType type, Boolean available) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("author"), pattern)
                ));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (available != null) {
                if (available) {
                    predicates.add(cb.isNull(root.get("borrowedAt")));
                } else {
                    predicates.add(cb.isNotNull(root.get("borrowedAt")));
                }
            }

            // System.out.println(predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
