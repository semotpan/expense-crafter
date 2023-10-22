package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "spending_plan")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE, force = true)
public class Plan extends AbstractAggregateRoot<Plan> {

    static final int MAX_NAME_LENGTH = 100;

    @EmbeddedId
    private final PlanIdentifier id;

    private final Instant creationTimestamp;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private final AccountIdentifier account;

    @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount amount;

    private String name;
    private String description;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private final List<Jar> jars = new ArrayList<>();

    @Builder
    public Plan(AccountIdentifier account,
                MonetaryAmount amount,
                String name,
                String description) {
        this.id = new PlanIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");
        this.amount = requireValidAmount(amount);
        this.name = requireValidName(name);
        this.description = description; // TODO check for max-length
    }

    private MonetaryAmount requireValidAmount(MonetaryAmount amount) {
        requireNonNull(amount, "amount cannot be null");

        if (amount.isLessThanOrEqualTo(Money.of(BigDecimal.ZERO, amount.getCurrency()))) {
            throw new IllegalArgumentException("amount must be positive value");
        }

        return amount;
    }

    private String requireValidName(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("name cannot be empty");

        if (name.trim().length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("name length cannot be more than " + MAX_NAME_LENGTH);

        return name.trim();
    }

    public void addJars(List<Jar> jars) {
        this.jars.addAll(requireNonNull(jars, "jars cannot be null"));
    }

    @Embeddable
    public record PlanIdentifier(UUID id) implements Serializable {

        public PlanIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
