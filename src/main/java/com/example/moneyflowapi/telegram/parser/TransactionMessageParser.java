package com.example.moneyflowapi.telegram.parser;

import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.transaction.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TransactionMessageParser {

    private static final Pattern AMOUNT_TOKEN = Pattern.compile(
            "^(\\p{Sc})?(\\d+(?:[.,]\\d+)?)([\\p{L}\\p{Sc}'‘’ʻ]+)?$"
    );

    private static final Map<String, CurrencyCode> ALIASES = buildAliases();

    public Optional<ParsedTransaction> parse(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String input = text.trim();
        TransactionType type = TransactionType.EXPENSE;

        if (input.startsWith("+")) {
            type = TransactionType.INCOME;
            input = input.substring(1).trim();
        }

        if (input.isEmpty()) {
            return Optional.empty();
        }

        String[] tokens = input.split("\\s+");

        int amountIndex = -1;
        String amountValue = null;
        CurrencyCode currency = null;

        for (int i = 1; i < tokens.length; i++) {
            Matcher matcher = AMOUNT_TOKEN.matcher(tokens[i]);
            if (!matcher.matches()) {
                continue;
            }

            String prefix = matcher.group(1);
            String suffix = matcher.group(3);
            CurrencyCode prefixCurrency = prefix != null ? resolve(prefix) : null;
            CurrencyCode suffixCurrency = suffix != null ? resolve(suffix) : null;

            if (prefix != null && prefixCurrency == null) {
                continue;
            }
            if (suffix != null && suffixCurrency == null) {
                continue;
            }

            amountIndex = i;
            amountValue = matcher.group(2);
            currency = prefixCurrency != null ? prefixCurrency : suffixCurrency;
            break;
        }

        if (amountIndex == -1) {
            return Optional.empty();
        }

        String category = String.join(" ", Arrays.copyOfRange(tokens, 0, amountIndex)).trim();
        if (category.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal amount = new BigDecimal(amountValue.replace(',', '.'));
        if (amount.signum() <= 0) {
            return Optional.empty();
        }

        int noteStart = amountIndex + 1;

        if (currency == null && noteStart < tokens.length) {
            CurrencyCode nextCurrency = resolve(tokens[noteStart]);
            if (nextCurrency != null) {
                currency = nextCurrency;
                noteStart++;
            }
        }

        String note = noteStart < tokens.length
                ? String.join(" ", Arrays.copyOfRange(tokens, noteStart, tokens.length)).trim()
                : null;

        return Optional.of(new ParsedTransaction(type, category, amount, currency, note));
    }

    private static CurrencyCode resolve(String value) {
        return ALIASES.get(value.toLowerCase(Locale.ROOT));
    }

    private static Map<String, CurrencyCode> buildAliases() {
        Map<String, CurrencyCode> map = new HashMap<>();

        for (CurrencyCode code : CurrencyCode.values()) {
            map.put(code.name().toLowerCase(Locale.ROOT), code);
        }

        map.put("$", CurrencyCode.USD);
        map.put("€", CurrencyCode.EUR);
        map.put("£", CurrencyCode.GBP);
        map.put("₽", CurrencyCode.RUB);
        map.put("₸", CurrencyCode.KZT);
        map.put("₺", CurrencyCode.TRY);
        map.put("₩", CurrencyCode.KRW);
        map.put("₹", CurrencyCode.INR);
        map.put("₴", CurrencyCode.UAH);
        map.put("zł", CurrencyCode.PLN);

        map.put("sum", CurrencyCode.UZS);
        map.put("söm", CurrencyCode.UZS);
        map.put("som", CurrencyCode.UZS);
        map.put("so'm", CurrencyCode.UZS);
        map.put("so‘m", CurrencyCode.UZS);
        map.put("so’m", CurrencyCode.UZS);
        map.put("soʻm", CurrencyCode.UZS);

        map.put("dollar", CurrencyCode.USD);
        map.put("euro", CurrencyCode.EUR);
        map.put("rub", CurrencyCode.RUB);

        return map;
    }
}