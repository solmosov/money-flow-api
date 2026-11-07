package com.example.moneyflowapi.telegram.message;

import com.example.moneyflowapi.user.UserLang;

public enum BotText {

    WELCOME(
            """
                    Welcome to Money Flow!

                    Send your expenses and incomes as a regular message.

                    Examples:
                    Taxi 30000
                    Lunch 5 usd with friends
                    +Salary 5000000 uzs""",
            """
                    Money Flow botiga xush kelibsiz!

                    Xarajat va daromadlarni oddiy xabar ko'rinishida yuboring.

                    Misollar:
                    Taksi 30000
                    Tushlik 5 usd do'stlar bilan
                    +Oylik 5000000 uzs""",
            """
                    Добро пожаловать в Money Flow!

                    Отправляйте расходы и доходы обычным сообщением.

                    Примеры:
                    Такси 30000
                    Обед 5 usd с друзьями
                    +Зарплата 5000000 uzs"""
    ),

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
    TYPE_EXPENSE("Expense", "Xarajat", "Расход");

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