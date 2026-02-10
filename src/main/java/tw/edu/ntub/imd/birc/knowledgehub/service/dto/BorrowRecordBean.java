package tw.edu.ntub.imd.birc.knowledgehub.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordBean {
    private Long id;
    private String isbn;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
}
