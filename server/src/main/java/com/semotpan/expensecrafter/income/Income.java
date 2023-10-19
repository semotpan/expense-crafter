package com.semotpan.expensecrafter.income;

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
@Table(name = "income")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE, force = true)
public class Income extends AbstractAggregateRoot<Income> {

    @EmbeddedId
    private final IncomeIdentifier id;
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

    private LocalDate incomeDate;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private IncomeSource incomeSource;

    @Builder
    public Income(AccountIdentifier account,
                  MonetaryAmount amount,
                  PaymentType paymentType,
                  LocalDate incomeDate,
                  String description,
                  IncomeSource incomeSource) {
        this.id = new IncomeIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");

        this.amount = requireValidAmount(amount);
        this.paymentType = paymentType == null ? PaymentType.CARD : paymentType;
        this.incomeDate = incomeDate == null ? LocalDate.now() : incomeDate;
        this.description = description;
        this.incomeSource = requireNonNull(incomeSource, "incomeSource cannot be null");

        registerEvent(IncomeCreated.builder()
                .incomeId(this.id)
                .accountId(this.account)
                .incomeSourceId(this.incomeSource.getId())
                .amount(this.amount)
                .paymentType(this.paymentType)
                .build());
    }

    private MonetaryAmount requireValidAmount(MonetaryAmount amount) {
        requireNonNull(amount, "amount cannot be null");

        if (amount.isLessThanOrEqualTo(Money.of(BigDecimal.ZERO, amount.getCurrency()))) {
            throw new IllegalArgumentException("amount must be positive value");
        }

        return amount;
    }

    public void update(MonetaryAmount amount,
                       PaymentType paymentType,
                       LocalDate incomeDate,
                       String description,
                       IncomeSource incomeSource) {
        this.amount = requireValidAmount(amount);
        this.paymentType = paymentType == null ? PaymentType.CARD : paymentType;
        this.incomeDate = incomeDate == null ? LocalDate.now() : incomeDate;
        this.description = description;
        this.incomeSource = requireNonNull(incomeSource, "incomeSource cannot be null");

        registerEvent(IncomeUpdated.builder()
                .incomeId(this.id)
                .accountId(this.account)
                .incomeSourceId(this.incomeSource.getId())
                .amount(this.amount)
                .paymentType(this.paymentType)
                .build());
    }

    /**
     * hard delete, register {@link IncomeDeleted} event to be sent after hard deletion
     */
    public void markDeleted() {
        registerEvent(new IncomeDeleted(this.id, this.account, this.incomeSource.getId()));
    }

    @Embeddable
    public record IncomeIdentifier(UUID id) implements Serializable {

        public IncomeIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
