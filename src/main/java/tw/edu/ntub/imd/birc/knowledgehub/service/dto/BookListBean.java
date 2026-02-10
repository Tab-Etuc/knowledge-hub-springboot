package tw.edu.ntub.imd.birc.knowledgehub.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookListBean {
    private long totalCount;
    private List<BookBean> books;
}
