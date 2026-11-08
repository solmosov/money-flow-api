package com.example.moneyflowapi.telegram.service;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.CategoryIconResolver;
import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.telegram.exception.BotTransactionException;
import com.example.moneyflowapi.telegram.message.BotText;
import com.example.moneyflowapi.telegram.parser.ParsedTransaction;
import com.example.moneyflowapi.transaction.TransactionService;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BotTransactionService {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999.99");
    private static final int MAX_CATEGORY_LENGTH = 50;

    private final CategoryIconResolver iconResolver;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public SavedTransaction save(User user, ParsedTransaction parsed) {
        UUID userId = user.getId();

        BigDecimal amount = parsed.amount().setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(MIN_AMOUNT) < 0 || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new BotTransactionException(BotText.AMOUNT_INVALID);
        }

        Wallet wallet = resolveWallet(userId, parsed);

        CategoryType categoryType = parsed.type() == TransactionType.INCOME
                ? CategoryType.INCOME
                : CategoryType.EXPENSE;

        Category category = resolveCategory(userId, categoryType, parsed.category());

        Instant now = Instant.now();

        TransactionResponse response = parsed.type() == TransactionType.INCOME
                ? transactionService.createIncome(
                userId,
                new TransactionIncomeRequest(wallet.getId(), category.getId(), amount, now, parsed.note())
        )
                : transactionService.createExpense(
                userId,
                new TransactionExpenseRequest(wallet.getId(), amount, category.getId(), now, parsed.note())
        );

        return new SavedTransaction(
                response.id(),
                parsed.type(),
                category.getName(),
                amount,
                wallet.getCurrency().getCode(),
                wallet.getName(),
                parsed.note()
        );
    }


    @Transactional
    public void delete(User user, UUID id) {
        transactionService.delete(user.getId(), id);
    }

    private Wallet resolveWallet(UUID userId, ParsedTransaction parsed) {
        List<Wallet> wallets = walletRepository.findAllByOwner(userId, false)
                .stream()
                .sorted(Comparator.comparing(Wallet::getCreatedAt))
                .toList();

        if (wallets.isEmpty()) {
            throw new BotTransactionException(BotText.NO_WALLET);
        }

        if (parsed.currency() == null) {
            return wallets.get(0);
        }

        return wallets.stream()
                .filter(w -> w.getCurrency().getCode() == parsed.currency())
                .findFirst()
                .orElseThrow(() -> new BotTransactionException(
                        BotText.WALLET_CURRENCY_NOT_FOUND,
                        parsed.currency().name()
                ));
    }

    private Category resolveCategory(UUID userId, CategoryType type, String rawName) {
        String name = rawName.strip();
        if (name.length() > MAX_CATEGORY_LENGTH) {
            name = name.substring(0, MAX_CATEGORY_LENGTH).strip();
        }

        String finalName = name;

        return categoryRepository
                .findFirstByUserIdAndTypeAndNameIgnoreCaseAndDeletedAtIsNull(userId, type, finalName)
                .orElseGet(() -> categoryRepository.saveAndFlush(
                        Category.builder()
                                .user(userRepository.getReferenceById(userId))
                                .name(finalName)
                                .type(type)
                                .iconName(iconResolver.resolve(type, finalName))
                                .build()
                ));
    }
}