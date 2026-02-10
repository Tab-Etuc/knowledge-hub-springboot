package tw.edu.ntub.imd.birc.practice.controller.assembler;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.service.dto.BookBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BookListBean;
import tw.edu.ntub.imd.birc.practice.service.dto.BorrowRecordBean;
import tw.edu.ntub.imd.birc.practice.service.strategy.BookStrategyFactory;
import tw.edu.ntub.imd.birc.practice.util.date.LocalDateTimeUtils;
import tw.edu.ntub.imd.birc.practice.util.json.array.ArrayData;
import tw.edu.ntub.imd.birc.practice.util.json.object.ObjectData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class BookResponseAssembler {

    private final BookStrategyFactory strategyFactory;

    public BookResponseAssembler(BookStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public ObjectData toListResponse(BookListBean bookListBean) {
        ArrayData booksArray = new ArrayData();
        for (BookBean book : bookListBean.getBooks()) {
            booksArray.addObject()
                    .add("isbn", book.getIsbn())
                    .add("title", book.getTitle())
                    .add("type", book.getType().name())
                    .add("classification", book.getClassification())
                    .add("isAvailable", book.getBorrowedAt() == null);
        }

        return new ObjectData()
                .add("total_count", bookListBean.getTotalCount())
                .add("books", booksArray);
    }

    public ObjectData toDetailResponse(BookBean book) {
        ObjectData data = new ObjectData()
                .add("isbn", book.getIsbn())
                .add("title", book.getTitle())
                .add("author", book.getAuthor())
                .add("type", book.getType().name())
                .add("publishedAt", book.getPublishedAt());

        addDateTimeField(data, "borrowedAt", book.getBorrowedAt());
        addDateTimeField(data, "returnedAt", book.getReturnedAt());

        data.add("classification", book.getClassification());

        strategyFactory.getStrategy(book.getType()).addResponseFields(book, data);

        return data;
    }

 
    public ArrayData toRecordsResponse(List<BorrowRecordBean> records) {
        ArrayData recordsArray = new ArrayData();
        for (BorrowRecordBean record : records) {
            ObjectData recordData = recordsArray.addObject();
            addDateTimeField(recordData, "borrowedAt", record.getBorrowedAt());
            addDateTimeField(recordData, "returnedAt", record.getReturnedAt());
        }
        return recordsArray;
    }

    private void addDateTimeField(ObjectData data, String key, LocalDateTime dateTime) {
        data.add(key, Optional.ofNullable(dateTime)
                .map(LocalDateTimeUtils::formatIso8601)
                .orElse(null));
    }
}
