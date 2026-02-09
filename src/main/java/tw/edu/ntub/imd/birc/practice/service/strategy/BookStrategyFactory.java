package tw.edu.ntub.imd.birc.practice.service.strategy;

import org.springframework.stereotype.Component;
import tw.edu.ntub.imd.birc.practice.databaseconfig.entity.enumerate.BookType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BookStrategyFactory {
    private final Map<BookType, BookTypeStrategy> strategies;

    public BookStrategyFactory(List<BookTypeStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(BookTypeStrategy::getType, s -> s));
    }

    public BookTypeStrategy getStrategy(BookType type) {
        BookTypeStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支援的書籍類型: " + type);
        }
        return strategy;
    }
}
