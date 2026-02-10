package tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao;

import org.springframework.stereotype.Repository;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.BorrowRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordDAO extends BaseDAO<BorrowRecord, Long> {

    List<BorrowRecord> findByIsbnOrderByBorrowedAtDesc(String isbn);

    Optional<BorrowRecord> findFirstByIsbnAndReturnedAtIsNullOrderByBorrowedAtDesc(String isbn);
}
