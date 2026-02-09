package tw.edu.ntub.imd.birc.practice.service.strategy;

import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;

public interface BookTypeStrategy {
    void validate(BookBean bean);                        // 驗證特定欄位
    String generateClassification(BookBean bean);        // 產生分類號
    void createDetail(Book book, BookBean bean);         // 建立明細（聚合根操作）
    void updateDetail(Book book, BookBean bean);         // 更新明細
    void mergeUpdatedFields(BookBean full, BookBean update); // 合併更新欄位
    BookType getType();                                  // 對應書籍類型
}
