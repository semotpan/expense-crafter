package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.shared.PaymentType;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    @lombok.Builder
    public Expense(AccountIdentifier account,
                   MonetaryAmount amount,
                   PaymentType paymentType,
                   LocalDate expenseDate,
                   String description,
                   Category category) {

        this.id = new ExpenseIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");
        this.category = requireNonNull(category, "category cannot be null");
        this.amount = requireValidAmount(amount);
        this.paymentType = paymentType == null ? PaymentType.CARD : paymentType;
        this.expenseDate = expenseDate == null ? LocalDate.now() : expenseDate;
        this.description = description;
    }

    private MonetaryAmount requireValidAmount(MonetaryAmount amount) {
        requireNonNull(amount, "account cannot be null");

        if (amount.isLessThanOrEqualTo(Money.of(BigDecimal.ZERO, amount.getCurrency()))) {
            throw new IllegalArgumentException("amount must be positive value");
        }

        return amount;
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
