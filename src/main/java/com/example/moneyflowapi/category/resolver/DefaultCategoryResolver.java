package com.example.moneyflowapi.category.resolver;

import com.example.moneyflowapi.category.CategoryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class DefaultCategoryResolver {

    public record DefaultCategory(String name, String iconName, CategoryType type) {
    }

    private static final String DEFAULT_LANGUAGE = "en";

    private static final Map<String, List<DefaultCategory>> CATEGORIES = Map.of(
            "en", List.of(
                    new DefaultCategory("Food", "Utensils", CategoryType.EXPENSE),
                    new DefaultCategory("Transport", "Bus", CategoryType.EXPENSE),
                    new DefaultCategory("Shopping", "ShoppingCart", CategoryType.EXPENSE),
                    new DefaultCategory("Utilities", "Zap", CategoryType.EXPENSE),
                    new DefaultCategory("Salary", "Banknote", CategoryType.INCOME),
                    new DefaultCategory("Other income", "Coins", CategoryType.INCOME)
            ),
            "uz", List.of(
                    new DefaultCategory("Oziq-ovqat", "Utensils", CategoryType.EXPENSE),
                    new DefaultCategory("Transport", "Bus", CategoryType.EXPENSE),
                    new DefaultCategory("Xaridlar", "ShoppingCart", CategoryType.EXPENSE),
                    new DefaultCategory("Kommunal xizmatlar", "Zap", CategoryType.EXPENSE),
                    new DefaultCategory("Maosh", "Banknote", CategoryType.INCOME),
                    new DefaultCategory("Boshqa daromad", "Coins", CategoryType.INCOME)
            ),
            "ru", List.of(
                    new DefaultCategory("Еда", "Utensils", CategoryType.EXPENSE),
                    new DefaultCategory("Транспорт", "Bus", CategoryType.EXPENSE),
                    new DefaultCategory("Покупки", "ShoppingCart", CategoryType.EXPENSE),
                    new DefaultCategory("Коммунальные услуги", "Zap", CategoryType.EXPENSE),
                    new DefaultCategory("Зарплата", "Banknote", CategoryType.INCOME),
                    new DefaultCategory("Прочие доходы", "Coins", CategoryType.INCOME)
            )
    );

    public List<DefaultCategory> resolve(String acceptLanguage) {
        return CATEGORIES.get(normalize(acceptLanguage));
    }

    private String normalize(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return DEFAULT_LANGUAGE;
        }

        String first = acceptLanguage.split(",")[0].split(";")[0].trim();
        String code = first.split("[-_]")[0].toLowerCase(Locale.ROOT);

        return CATEGORIES.containsKey(code) ? code : DEFAULT_LANGUAGE;
    }
}