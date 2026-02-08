package tw.edu.ntub.imd.birc.practice.service;

import tw.edu.ntub.imd.birc.practice.service.dto.BorrowRecordBean;

import java.util.List;

public interface BorrowRecordService extends BaseService<BorrowRecordBean, Long> {

    List<BorrowRecordBean> getRecordsByIsbn(String isbn);
}
