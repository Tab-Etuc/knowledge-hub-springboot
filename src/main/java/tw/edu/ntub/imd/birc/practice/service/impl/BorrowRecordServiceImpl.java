package tw.edu.ntub.imd.birc.practice.service.impl;

import org.springframework.stereotype.Service;
import tw.edu.ntub.imd.birc.practice.databaseconfig.dao.BorrowRecordDAO;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.BorrowRecord;
import tw.edu.ntub.imd.birc.practice.service.BorrowRecordService;
import tw.edu.ntub.imd.birc.practice.service.dto.BorrowRecordBean;
import tw.edu.ntub.imd.birc.practice.service.transformer.BorrowRecordTransformer;
import tw.edu.ntub.birc.common.util.CollectionUtils;

import java.util.List;

@Service
public class BorrowRecordServiceImpl extends BaseServiceImpl<BorrowRecordBean, BorrowRecord, Long> implements BorrowRecordService {

    private final BorrowRecordDAO borrowRecordDAO;
    private final BorrowRecordTransformer borrowRecordTransformer;

    public BorrowRecordServiceImpl(BorrowRecordDAO borrowRecordDAO,
                                   BorrowRecordTransformer borrowRecordTransformer) {
        super(borrowRecordDAO, borrowRecordTransformer);
        this.borrowRecordDAO = borrowRecordDAO;
        this.borrowRecordTransformer = borrowRecordTransformer;
    }

    @Override
    public BorrowRecordBean save(BorrowRecordBean bean) {
        BorrowRecord entity = borrowRecordTransformer.transferToEntity(bean);
        entity.setSave(true);
        BorrowRecord saved = borrowRecordDAO.save(entity);
        return borrowRecordTransformer.transferToBean(saved);
    }

    @Override
    public List<BorrowRecordBean> getRecordsByIsbn(String isbn) {
        List<BorrowRecord> records = borrowRecordDAO.findByIsbnOrderByBorrowedAtDesc(isbn);
        return CollectionUtils.map(records, borrowRecordTransformer::transferToBean);
    }
}
