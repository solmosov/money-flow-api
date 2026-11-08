package com.example.moneyflowapi.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CategoryIconResolver {

    public static final String FALLBACK_ICON = "CircleQuestionMark";

    private static final List<String> LANGS = List.of("en", "uz", "ru");
    private static final int MIN_SCORE = 10;
    private static final Pattern APOSTROPHES = Pattern.compile("[ʻʼ'‘’`´]");
    private static final Pattern SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");

    private final Map<CategoryType, List<Entry>> entries = new EnumMap<>(CategoryType.class);

    public CategoryIconResolver(ObjectMapper objectMapper) {
        entries.put(CategoryType.EXPENSE, new ArrayList<>());
        entries.put(CategoryType.INCOME, new ArrayList<>());

        for (String lang : LANGS) {
            try (InputStream in = new ClassPathResource("icons/" + lang + ".json").getInputStream()) {
                Map<String, Object> root = objectMapper.readValue(in, new TypeReference<Map<String, Object>>() {
                });
                addSection(CategoryType.EXPENSE, root.get("expense"));
                addSection(CategoryType.INCOME, root.get("income"));
            } catch (Exception e) {
                log.error("Failed to load icons/{}.json", lang, e);
            }
        }
    }

    public String resolve(CategoryType type, String name) {
        if (name == null || name.isBlank()) {
            return FALLBACK_ICON;
        }

        String phrase = normalize(name);
        List<String> tokens = tokenize(phrase);

        String bestIcon = FALLBACK_ICON;
        int bestScore = 0;

        for (Entry entry : entries.getOrDefault(type, List.of())) {
            int score = score(entry, phrase, tokens);
            if (score > bestScore) {
                bestScore = score;
                bestIcon = entry.icon();
            }
        }

        return bestIcon;
    }

    private void addSection(CategoryType type, Object section) {
        if (!(section instanceof Map<?, ?> icons)) {
            return;
        }

        List<Entry> target = entries.get(type);

        for (Map.Entry<?, ?> item : icons.entrySet()) {
            String icon = String.valueOf(item.getKey());

            if (!(item.getValue() instanceof Map<?, ?> data)) {
                continue;
            }

            Object label = data.get("label");
            Object keywords = data.get("keywords");

            if (label instanceof String text) {
                addEntry(target, icon, text, true);
            }

            if (keywords instanceof String text) {
                for (String keyword : text.split(",")) {
                    addEntry(target, icon, keyword, false);
                }
            }
        }
    }

    private void addEntry(List<Entry> target, String icon, String raw, boolean label) {
        String phrase = normalize(raw);
        if (phrase.isEmpty()) {
            return;
        }
        target.add(new Entry(icon, phrase, tokenize(phrase), label));
    }

    private int score(Entry entry, String phrase, List<String> tokens) {
        if (entry.phrase().equals(phrase)) {
            return entry.label() ? 100 : 80;
        }

        int total = 0;

        for (String token : tokens) {
            if (token.length() < 3) {
                continue;
            }

            int best = 0;

            for (String word : entry.words()) {
                if (word.equals(token)) {
                    best = Math.max(best, entry.label() ? 30 : 20);
                } else if (stemMatch(token, word)) {
                    best = Math.max(best, entry.label() ? 12 : 8);
                }
            }

            total += best;
        }

        return total >= MIN_SCORE ? total : 0;
    }

    private boolean stemMatch(String a, String b) {
        if (a.length() < 4 || b.length() < 4) {
            return false;
        }

        int limit = Math.min(a.length(), b.length());
        int common = 0;

        while (common < limit && a.charAt(common) == b.charAt(common)) {
            common++;
        }

        return common >= Math.max(4, limit - 2);
    }

    private static String normalize(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        text = APOSTROPHES.matcher(text).replaceAll("");
        text = text
                .replace("ş", "sh")
                .replace("ç", "ch")
                .replace("ö", "o")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ı", "i")
                .replace("ё", "е");
        return text.strip();
    }

    private static List<String> tokenize(String phrase) {
        return Arrays.stream(SPLIT.split(phrase))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private record Entry(String icon, String phrase, List<String> words, boolean label) {
    }
}