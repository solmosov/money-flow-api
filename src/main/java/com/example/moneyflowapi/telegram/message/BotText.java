package com.example.moneyflowapi.telegram.message;

import com.example.moneyflowapi.user.UserLang;

public enum BotText {
    PARSE_ERROR(
            """
                    Could not understand the message.

                    Format:
                    Category Amount [Currency] [Note]
                    +Category Amount [Currency] [Note]

                    Examples:
                    Taxi 30000
                    Lunch 5 usd with friends
                    +Salary 5000000 uzs""",
            """
                    Xabar tushunarsiz.

                    Format:
                    Kategoriya Summa [Valyuta] [Izoh]
                    +Kategoriya Summa [Valyuta] [Izoh]

                    Misollar:
                    Taksi 30000
                    Tushlik 5 usd do'stlar bilan
                    +Oylik 5000000 uzs""",
            """
                    Не удалось понять сообщение.

                    Формат:
                    Категория Сумма [Валюта] [Заметка]
                    +Категория Сумма [Валюта] [Заметка]

                    Примеры:
                    Такси 30000
                    Обед 5 usd с друзьями
                    +Зарплата 5000000 uzs"""
    ),

    TRANSACTION_PARSED(
            """
                    Type: %s
                    Category: %s
                    Amount: %s %s
                    Note: %s""",
            """
                    Turi: %s
                    Kategoriya: %s
                    Summa: %s %s
                    Izoh: %s""",
            """
                    Тип: %s
                    Категория: %s
                    Сумма: %s %s
                    Заметка: %s"""
    ),

    TYPE_INCOME("Income", "Daromad", "Доход"),
    TYPE_EXPENSE("Expense", "Xarajat", "Расход"),
    TRANSACTION_SAVED(
            """
                    <b>Saved</b>
                    
                    Type: %s
                    Category: %s
                    Amount: <b>%s %s</b>
                    Wallet: %s
                    Note: %s""",
            """
                    <b>Saqlandi</b>
                    
                    Turi: %s
                    Kategoriya: %s
                    Summa: <b>%s %s</b>
                    Hamyon: %s
                    Izoh: %s""",
            """
                    <b>Сохранено</b>
                    
                    Тип: %s
                    Категория: %s
                    Сумма: <b>%s %s</b>
                    Кошелёк: %s
                    Заметка: %s"""
    ),

    NO_WALLET(
            "You have no active wallet. Create one in the app first.",
            "Faol hamyoningiz yöq. Avval ilovada hamyon yarating.",
            "У вас нет активного кошелька. Сначала создайте его в приложении."
    ),

    WALLET_CURRENCY_NOT_FOUND(
            "You have no %s wallet. Create one in the app or use another currency.",
            "%s valyutasida hamyoningiz yöq. Ilovada yarating yoki boşqa valyuta yozing.",
            "У вас нет кошелька в валюте %s. Создайте его в приложении или укажите другую валюту."
    ),

    AMOUNT_INVALID(
            "Invalid amount. It must be between 0.01 and 999999999999.99.",
            "Summa notöğri. U 0.01 dan 999999999999.99 gaça bölişi kerak.",
            "Неверная сумма. Она должна быть от 0.01 до 999999999999.99."
    ),

    SAVE_FAILED(
            "Could not save the transaction. Please try again.",
            "Tranzaksiyani saqlab bölmadi. Qayta urinib köring.",
            "Не удалось сохранить транзакцию. Попробуйте ещё раз."
    );

    private final String en;
    private final String uz;
    private final String ru;

    BotText(String en, String uz, String ru) {
        this.en = en;
        this.uz = uz;
        this.ru = ru;
    }

    public String get(UserLang lang, Object... args) {
        UserLang resolved = lang == null ? UserLang.UZ : lang;
        String template = switch (resolved) {
            case EN -> en;
            case RU -> ru;
            case UZ -> uz;
        };
        return args.length == 0 ? template : template.formatted(args);
    }
}