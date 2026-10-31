package com.example.moneyflowapi.demo;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.transaction.Transaction;
import com.example.moneyflowapi.transaction.TransactionRepository;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "demo-data.enabled", havingValue = "true")
public class DemoDataJob {

    private static final int MAX_CATCH_UP_DAYS = 31;
    private static final int MIN_EXPENSES_PER_DAY = 2;
    private static final int MAX_EXPENSES_PER_DAY = 4;

    private record Profile(String code, int idx, UUID userId, String currency, String foreignCurrency,
                           BigDecimal priceLevel, BigDecimal step, int salaryUnits) {
    }

    private record Cat(String key, String type, String[] names, String[][] descs,
                       int min, int max, int weight) {
    }

    private record Draft(Instant date, TransactionType type, String wallet, String toWallet,
                         String cat, BigDecimal amount, BigDecimal toAmount, String desc) {
    }

    private static final Map<String, String[]> WALLET_NAMES = Map.of(
            "cash", new String[]{"Cash", "Naqd pul", "Наличные"},
            "card", new String[]{"Main Card", "Asosiy karta", "Основная карта"},
            "savings", new String[]{"Savings", "Jamğarma", "Накопления"},
            "foreign", new String[]{"Foreign Currency", "Valyuta hisobi", "Валютный счёт"}
    );

    private static final List<Profile> PROFILES = List.of(
            new Profile("en", 0, UUID.fromString("421978c0-707c-441f-a1a8-79167c4e5cee"),
                    "USD", "EUR", new BigDecimal("1"), new BigDecimal("1"), 3500),
            new Profile("uz", 1, UUID.fromString("65fbce0f-b82f-4282-8a89-13d43d3b444c"),
                    "UZS", "USD", new BigDecimal("7000"), new BigDecimal("1000"), 3000),
            new Profile("ru", 2, UUID.fromString("bcb228ee-0eb6-4167-8561-60932edf1595"),
                    "RUB", "USD", new BigDecimal("70"), new BigDecimal("10"), 3200)
    );

    private static final Cat SALARY = new Cat("salary", "INCOME",
            new String[]{"Salary", "Maosh", "Зарплата"},
            new String[][]{{"Monthly salary"}, {"Oylik maosh"}, {"Ежемесячная зарплата"}},
            0, 0, 0);

    private static final Cat FREELANCE = new Cat("freelance", "INCOME",
            new String[]{"Freelance", "Frilans", "Фриланс"},
            new String[][]{{"Side project", "Website order"}, {"Qöşimcha loyiha", "Sayt buyurtmasi"}, {"Подработка", "Заказ на сайт"}},
            50, 400, 0);

    private static final List<Cat> EXPENSE_CATS = List.of(
            new Cat("groceries", "EXPENSE",
                    new String[]{"Groceries", "Oziq-ovqat", "Продукты"},
                    new String[][]{{"Weekly groceries", "Supermarket"}, {"Haftalik xarid", "Supermarket"}, {"Продукты на неделю", "Супермаркет"}},
                    5, 40, 30),
            new Cat("cafe", "EXPENSE",
                    new String[]{"Cafe & Restaurants", "Kafe va restoranlar", "Кафе и рестораны"},
                    new String[][]{{"Coffee with friends", "Lunch"}, {"Döstlar bilan qahva", "Tushlik"}, {"Кофе с друзьями", "Обед"}},
                    3, 25, 20),
            new Cat("transport", "EXPENSE",
                    new String[]{"Transport", "Transport", "Транспорт"},
                    new String[][]{{"Taxi", "Metro card top-up"}, {"Taksi", "Metro kartasini töldirish"}, {"Такси", "Пополнение проездного"}},
                    1, 15, 18),
            new Cat("utilities", "EXPENSE",
                    new String[]{"Utilities", "Kommunal tölovlar", "Коммунальные услуги"},
                    new String[][]{{"Electricity and water", "Internet bill"}, {"Elektr va suv", "Internet tölovi"}, {"Электричество и вода", "Интернет"}},
                    20, 80, 3),
            new Cat("shopping", "EXPENSE",
                    new String[]{"Shopping", "Xaridlar", "Покупки"},
                    new String[][]{{"Clothes", "Home items"}, {"Kiyim-keçak", "Uy buyumlari"}, {"Одежда", "Товары для дома"}},
                    10, 120, 8),
            new Cat("health", "EXPENSE",
                    new String[]{"Health", "Soğliq", "Здоровье"},
                    new String[][]{{"Pharmacy", "Doctor visit"}, {"Dorixona", "Şifokor qabuli"}, {"Аптека", "Приём у врача"}},
                    5, 60, 4),
            new Cat("entertainment", "EXPENSE",
                    new String[]{"Entertainment", "Köngilochar", "Развлечения"},
                    new String[][]{{"Cinema", "Streaming subscription"}, {"Kino", "Obuna tölovi"}, {"Кино", "Подписка на сервис"}},
                    5, 50, 8)
    );

    private static final String[] TRANSFER_SAVINGS = {"Monthly savings", "Oylik jamğarma", "Ежемесячные накопления"};
    private static final String[] TRANSFER_FX = {"Currency purchase", "Valyuta xaridi", "Покупка валюты"};

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(cron = "${demo-data.cron:0 10 0 * * *}", zone = "UTC")
    public void run() {
        for (Profile profile : PROFILES) {
            try {
                transactionTemplate.executeWithoutResult(status -> generateForUser(profile));
            } catch (Exception e) {
                log.error("[{}] demo data generation failed", profile.code(), e);
            }
        }
    }

    private void generateForUser(Profile p) {
        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        Instant last = transactionRepository.findLastDate(p.userId());
        LocalDate start = yesterday;
        if (last != null) {
            LocalDate next = last.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1);
            start = next.isAfter(yesterday.minusDays(MAX_CATCH_UP_DAYS - 1L))
                    ? next
                    : yesterday.minusDays(MAX_CATCH_UP_DAYS - 1L);
        }
        if (start.isAfter(yesterday)) {
            log.info("[{}] demo data is up to date", p.code());
            return;
        }

        Map<String, Wallet> wallets = new HashMap<>();
        Map<String, Wallet> byName = new HashMap<>();
        for (Wallet w : walletRepository.findAllByOwner(p.userId(), false)) {
            byName.put(w.getName(), w);
        }
        WALLET_NAMES.forEach((key, names) -> {
            Wallet w = byName.get(names[p.idx()]);
            if (w != null) {
                wallets.put(key, w);
            }
        });
        if (!wallets.containsKey("card") || !wallets.containsKey("cash") || !wallets.containsKey("savings")) {
            log.warn("[{}] required wallets not found, skipping", p.code());
            return;
        }

        Map<String, Category> categories = new HashMap<>();
        Map<String, Category> catByName = new HashMap<>();
        for (Category c : categoryRepository.findByUserIdAndDeletedAtIsNull(p.userId())) {
            catByName.put(c.getName(), c);
        }
        List<Cat> allCats = new ArrayList<>(EXPENSE_CATS);
        allCats.add(SALARY);
        allCats.add(FREELANCE);
        for (Cat c : allCats) {
            Category category = catByName.get(c.names()[p.idx()]);
            if (category != null) {
                categories.put(c.key(), category);
            }
        }

        User user = userRepository.getReferenceById(p.userId());
        Currency currency = currencyRepository.getReferenceById(p.currency());

        List<Transaction> toSave = new ArrayList<>();
        for (LocalDate day = start; !day.isAfter(yesterday); day = day.plusDays(1)) {
            Random rnd = new Random(Objects.hash(p.code(), day.toEpochDay()));
            List<Draft> drafts = buildDrafts(p, day, rnd, wallets.get("foreign"));
            drafts.sort(Comparator.comparing(Draft::date));
            for (Draft d : drafts) {
                Transaction tx = apply(d, p, wallets, categories, user, currency);
                if (tx != null) {
                    toSave.add(tx);
                }
            }
        }

        transactionRepository.saveAll(toSave);
        walletRepository.saveAll(wallets.values());
        categoryRepository.saveAll(categories.values());
        log.info("[{}] generated {} transactions from {} to {}", p.code(), toSave.size(), start, yesterday);
    }

    private Transaction apply(Draft d, Profile p, Map<String, Wallet> wallets, Map<String, Category> categories,
                              User user, Currency currency) {
        Wallet source = wallets.get(d.wallet());
        Wallet target = d.toWallet() == null ? null : wallets.get(d.toWallet());
        Category category = d.cat() == null ? null : categories.get(d.cat());

        switch (d.type()) {
            case INCOME -> source.balanceIncrement(d.amount());
            case EXPENSE -> {
                source = pickWallet(wallets, d.wallet(), d.amount());
                if (source == null) {
                    log.warn("[{}] expense skipped, insufficient balance: {}", p.code(), d.amount());
                    return null;
                }
                source.balanceDecrement(d.amount());
            }
            case TRANSFER -> {
                if (target == null || source.getBalance().compareTo(d.amount()) < 0) {
                    log.warn("[{}] transfer skipped: {} -> {}", p.code(), d.wallet(), d.toWallet());
                    return null;
                }
                source.balanceDecrement(d.amount());
                target.balanceIncrement(d.toAmount());
            }
            default -> {
                return null;
            }
        }

        if (category != null) {
            category.setUsedAt(d.date());
        }

        return Transaction.builder()
                .user(user)
                .wallet(source)
                .toWallet(target)
                .category(category)
                .type(d.type())
                .amount(d.amount())
                .toAmount(d.toAmount())
                .rateAmount(BigDecimal.ONE)
                .currency(currency)
                .date(d.date())
                .description(d.desc())
                .build();
    }

    private Wallet pickWallet(Map<String, Wallet> wallets, String preferred, BigDecimal amount) {
        List<String> order = List.of(preferred, preferred.equals("card") ? "cash" : "card", "savings");
        for (String key : order) {
            Wallet w = wallets.get(key);
            if (w != null && w.getBalance().compareTo(amount) >= 0) {
                return w;
            }
        }
        return null;
    }

    private List<Draft> buildDrafts(Profile p, LocalDate day, Random rnd, Wallet foreignWallet) {
        List<Draft> drafts = new ArrayList<>();
        BigDecimal salary = money(p, p.salaryUnits());
        int dom = day.getDayOfMonth();

        if (dom == 5) {
            drafts.add(new Draft(at(day, 9, rnd), TransactionType.INCOME, "card", null, SALARY.key(),
                    salary, null, desc(SALARY, p, rnd)));
        }
        if (dom == 10) {
            BigDecimal amount = round(salary.multiply(new BigDecimal("0.2")), p.step());
            drafts.add(new Draft(at(day, 10, rnd), TransactionType.TRANSFER, "card", "savings", null,
                    amount, amount, TRANSFER_SAVINGS[p.idx()]));
        }
        if (dom == 20 && foreignWallet != null) {
            BigDecimal amount = round(salary.multiply(new BigDecimal("0.1")), p.step());
            BigDecimal toAmount = amount.divide(foreignWallet.getRateAmount(), 2, RoundingMode.HALF_UP);
            drafts.add(new Draft(at(day, 11, rnd), TransactionType.TRANSFER, "card", "foreign", null,
                    amount, toAmount, TRANSFER_FX[p.idx()]));
        }
        if (rnd.nextInt(100) < 4) {
            BigDecimal amount = money(p, FREELANCE.min() + rnd.nextDouble() * (FREELANCE.max() - FREELANCE.min()));
            drafts.add(new Draft(at(day, 15, rnd), TransactionType.INCOME, "card", null, FREELANCE.key(),
                    amount, null, desc(FREELANCE, p, rnd)));
        }

        int totalWeight = EXPENSE_CATS.stream().mapToInt(Cat::weight).sum();
        int count = MIN_EXPENSES_PER_DAY + rnd.nextInt(MAX_EXPENSES_PER_DAY - MIN_EXPENSES_PER_DAY + 1);
        for (int k = 0; k < count; k++) {
            Cat c = pick(totalWeight, rnd);
            BigDecimal amount = money(p, c.min() + rnd.nextDouble() * (c.max() - c.min()));
            String wallet = rnd.nextInt(100) < 65 ? "card" : "cash";
            drafts.add(new Draft(at(day, 7 + rnd.nextInt(15), rnd), TransactionType.EXPENSE, wallet, null, c.key(),
                    amount, null, desc(c, p, rnd)));
        }
        return drafts;
    }

    private Cat pick(int totalWeight, Random rnd) {
        int r = rnd.nextInt(totalWeight);
        for (Cat c : EXPENSE_CATS) {
            r -= c.weight();
            if (r < 0) {
                return c;
            }
        }
        return EXPENSE_CATS.get(0);
    }

    private String desc(Cat c, Profile p, Random rnd) {
        String[] options = c.descs()[p.idx()];
        return options[rnd.nextInt(options.length)];
    }

    private Instant at(LocalDate day, int hour, Random rnd) {
        return day.atTime(hour, rnd.nextInt(60), rnd.nextInt(60)).toInstant(ZoneOffset.UTC);
    }

    private BigDecimal money(Profile p, double units) {
        return round(BigDecimal.valueOf(units).multiply(p.priceLevel()), p.step());
    }

    private BigDecimal round(BigDecimal value, BigDecimal step) {
        BigDecimal r = value.divide(step, 0, RoundingMode.HALF_UP).multiply(step);
        if (r.compareTo(step) < 0 && value.signum() > 0) {
            r = step;
        }
        return r.setScale(2, RoundingMode.HALF_UP);
    }
}