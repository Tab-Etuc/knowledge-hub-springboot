package tw.edu.ntub.imd.birc.practice.service.transformer;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.BorrowRecord;
import tw.edu.ntub.imd.birc.practice.service.dto.BorrowRecordBean;

import javax.annotation.Nonnull;

@Component
public class BorrowRecordTransformer implements BeanEntityTransformer<BorrowRecordBean, BorrowRecord> {

    @Nonnull
    @Override
    public BorrowRecord transferToEntity(@Nonnull BorrowRecordBean bean) {
        BorrowRecord record = new BorrowRecord();
        record.setId(bean.getId());
        record.setIsbn(bean.getIsbn());
        record.setBorrowedAt(bean.getBorrowedAt());
        record.setReturnedAt(bean.getReturnedAt());
        return record;
    }

    @Nonnull
    @Override
    public BorrowRecordBean transferToBean(@Nonnull BorrowRecord record) {
        BorrowRecordBean bean = new BorrowRecordBean();
        bean.setId(record.getId());
        bean.setIsbn(record.getIsbn());
        bean.setBorrowedAt(record.getBorrowedAt());
        bean.setReturnedAt(record.getReturnedAt());
        return bean;
    }
}
