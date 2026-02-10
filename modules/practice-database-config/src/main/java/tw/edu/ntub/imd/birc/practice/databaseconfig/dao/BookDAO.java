package tw.edu.ntub.imd.birc.practice.databaseconfig.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;

@Repository
public interface BookDAO extends BaseDAO<Book, String>, JpaSpecificationExecutor<Book> {
}
