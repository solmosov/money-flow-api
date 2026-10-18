package com.example.moneyflowapi.common.validator;

import com.example.moneyflowapi.common.annotation.SafeText;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SafeTextValidator implements ConstraintValidator<SafeText, String> {
    private int max;
    private boolean allowBlank;
    private boolean blockFormulaPrefix;

    private static final Pattern MENTION = Pattern.compile("@[\\p{L}\\p{N}_.]{1,50}");

    @Override
    public void initialize(SafeText annotation) {
        this.max = annotation.max();
        this.allowBlank = annotation.allowBlank();
        this.blockFormulaPrefix = annotation.blockFormulaPrefix();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.codePointCount(0, value.length()) > max) {
            return false;
        }
        int i = 0;
        while (i < value.length()) {
            int cp = value.codePointAt(i);
            i += Character.charCount(cp);
            if (!isAllowed(cp)) {
                return false;
            }
        }
        String stripped = value.strip();
        if (stripped.isEmpty()) {
            return allowBlank;
        }


        if (blockFormulaPrefix) {
            String folded = Normalizer.normalize(stripped, Normalizer.Form.NFKC);
            char first = folded.charAt(0);
            if (first == '=' || first == '+' || first == '-') {
                return false;
            }
            if (first == '@' && !MENTION.matcher(folded).matches()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllowed(int cp) {
        if (cp == ' ') {
            return true;
        }
        if (cp == '<' || cp == '>') {
            return false;
        }
        return switch (Character.getType(cp)) {
            case Character.CONTROL,
                 Character.FORMAT,
                 Character.LINE_SEPARATOR,
                 Character.PARAGRAPH_SEPARATOR,
                 Character.SPACE_SEPARATOR,
                 Character.PRIVATE_USE,
                 Character.SURROGATE,
                 Character.UNASSIGNED -> false;
            default -> true;
        };
    }
}
