package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.shared.PaymentType;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "expense")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE, force = true)
public class Expense extends AbstractAggregateRoot<Expense> {

    @EmbeddedId
    private final ExpenseIdentifier id;

    private final Instant creationTimestamp;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private final AccountIdentifier account;

    @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private LocalDate expenseDate;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Builder
    public Expense(AccountIdentifier account,
                   MonetaryAmount amount,
                   PaymentType paymentType,
                   LocalDate expenseDate,
                   String description,
                   Category category) {

        this.id = new ExpenseIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");

        this.amount = requireValidAmount(amount);
        category(category);
        paymentType(paymentType);
        expenseDate(expenseDate);
        this.description = description;

        registerEvent(ExpenseCreated.builder()
                .expenseId(this.id)
                .accountId(this.account)
                .categoryId(this.category.getId())
                .amount(this.amount)
                .paymentType(this.paymentType)
                .build());
    }

    void update(MonetaryAmount amount,
                PaymentType paymentType,
                LocalDate expenseDate,
                String description,
                Category category) {
        this.amount = requireValidAmount(amount);
        this.description = description;
        category(category);
        paymentType(paymentType);
        expenseDate(expenseDate);

        registerEvent(ExpenseUpdated.builder()
                .expenseId(this.id)
                .accountId(this.account)
                .categoryId(this.category.getId())
                .amount(this.amount)
                .paymentType(this.paymentType)
                .build());
    }

    private void category(Category category) {
        this.category = requireNonNull(category, "category cannot be null");
    }

    private void paymentType(PaymentType paymentType) {
        this.paymentType = paymentType == null ? PaymentType.CARD : paymentType;
    }

    private void expenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate == null ? LocalDate.now() : expenseDate;
    }

    private MonetaryAmount requireValidAmount(MonetaryAmount amount) {
        requireNonNull(amount, "account cannot be null");

        if (amount.isLessThanOrEqualTo(Money.of(BigDecimal.ZERO, amount.getCurrency()))) {
            throw new IllegalArgumentException("amount must be positive value");
        }

        return amount;
    }

    /**
     * hard delete, register {@link ExpenseDeleted} event to be sent after hard deletion
     */
    public void markDeleted() {
        registerEvent(ExpenseDeleted.builder()
                .expenseId(this.id)
                .accountId(this.account)
                .categoryId(this.category.getId())
                .build());
    }

    @Embeddable
    public record ExpenseIdentifier(UUID id) implements Serializable {

        public ExpenseIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
