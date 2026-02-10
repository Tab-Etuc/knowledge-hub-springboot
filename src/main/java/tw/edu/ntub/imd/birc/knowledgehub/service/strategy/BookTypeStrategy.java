package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

public interface BookTypeStrategy {
    void validate(BookBean bean);                        // 驗證特定欄位
    String generateClassification(BookBean bean);        // 產生分類號

    // 建立明細（如訂單明細）必須透過聚合根（如訂單）進行，以確保資料一致性。
    // 外部不應直接操作明細，而應呼叫聚合根的「新增明細」方法，由聚合根更新自身狀態並維護總金額等不變性，
    // 最後以 Transaction 形式將整個聚合一併存入資料庫。 
    void createDetail(Book book, BookBean bean);         // 建立明細（聚合根操作）

    void updateDetail(Book book, BookBean bean);         // 更新明細
    void mergeUpdatedFields(BookBean full, BookBean update); // 合併更新欄位
    void populateBean(Book book, BookBean bean);         // Entity -> Bean 填充類型專屬欄位
    void addResponseFields(BookBean bean, ObjectData data); // 填充 API 回應的類型專屬欄位
    BookType getType();                                  // 對應書籍類型
}
