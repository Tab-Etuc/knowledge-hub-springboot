package tw.edu.ntub.imd.birc.knowledgehub.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.dao.ChineseBookDetailDAO;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.Book;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.ChineseBookDetail;
import tw.edu.ntub.imd.birc.knowledgehub.databaseconfig.entity.enumerate.BookType;
import tw.edu.ntub.imd.birc.knowledgehub.exception.ChineseBookCodeInvalidException;
import tw.edu.ntub.imd.birc.knowledgehub.service.dto.BookBean;
import tw.edu.ntub.imd.birc.knowledgehub.util.json.object.ObjectData;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChineseBookStrategy extends AbstractBookTypeStrategy {

    private final ChineseBookDetailDAO chineseBookDetailDAO;

    private static final String CHINESE_DDC_PATTERN = "^[A-Z]\\d.*$";

    @Override
    public void validate(BookBean bean) {
        validateRequired(bean.getChineseDdcCode(), "chineseDdcCode");
        validatePattern(bean.getChineseDdcCode(), CHINESE_DDC_PATTERN, new ChineseBookCodeInvalidException());
    }

    @Override
    public String generateClassification(BookBean bean) {
        return bean.getChineseDdcCode();
    }

    @Override
    public void createDetail(Book book, BookBean bean) {
        ChineseBookDetail detail = new ChineseBookDetail();
        detail.setIsbn(book.getIsbn());
        detail.setChineseDdcCode(bean.getChineseDdcCode());
        chineseBookDetailDAO.save(detail);
    }

    @Override
    public void updateDetail(Book book, BookBean bean) {
        if (bean.getChineseDdcCode() != null) {
            validatePattern(bean.getChineseDdcCode(), CHINESE_DDC_PATTERN, new ChineseBookCodeInvalidException());
            chineseBookDetailDAO.findById(book.getIsbn())
                    .ifPresent(detail -> detail.setChineseDdcCode(bean.getChineseDdcCode()));
        }
    }

    @Override
    public void mergeUpdatedFields(BookBean fullBean, BookBean updateBean) {
        Optional.ofNullable(updateBean.getChineseDdcCode()).ifPresent(fullBean::setChineseDdcCode);
    }

    @Override
    public void populateBean(Book book, BookBean bean) {
        chineseBookDetailDAO.findById(book.getIsbn())
                .ifPresent(detail -> bean.setChineseDdcCode(detail.getChineseDdcCode()));
    }

    @Override
    public void addResponseFields(BookBean bean, ObjectData data) {
        data.add("chineseDdcCode", bean.getChineseDdcCode());
    }

    @Override
    public BookType getType() {
        return BookType.CHINESE;
    }
}
