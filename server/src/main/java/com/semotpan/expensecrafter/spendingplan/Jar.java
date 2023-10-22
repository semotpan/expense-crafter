package com.semotpan.expensecrafter.spendingplan;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CompositeType;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "spending_jar")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE, force = true)
public class Jar {

    static final int MAX_NAME_LENGTH = 100;

    @EmbeddedId
    private final JarIdentifier id;

    private final Instant creationTimestamp;

    @AttributeOverride(name = "amount", column = @Column(name = "amount_to_reach"))
    @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount amountToReach;

    private String name;
    private Integer percentage;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spending_plan_id", referencedColumnName = "id")
    private Plan plan;

    @Builder
    public Jar(String name,
               Integer percentage,
               String description,
               Plan plan) {
        this.id = new JarIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.plan = requireNonNull(plan, "plan cannot be null");
        this.percentage = requireValidPercentage(percentage);
        this.name = requireValidName(name);
        this.description = description; // TODO check for max-length
        this.amountToReach = amountToReach();
    }

    private Integer requireValidPercentage(Integer percentage) {
        requireNonNull(percentage, "percentage cannot be null");

        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("percentage must be between 0 and 100");

        return percentage;
    }

    /**
     * Calculates amount's percentage from total plan amount
     * x = amount * (percentage / 100)
     *
     * @return the calculated {@link MonetaryAmount} from total
     */
    private MonetaryAmount amountToReach() {
        return plan.getAmount().multiply((double) percentage * 0.01);
    }

    private String requireValidName(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("name cannot be empty");

        if (name.trim().length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("name length cannot be more than " + MAX_NAME_LENGTH);

        return name.trim();
    }

    @Embeddable
    public record JarIdentifier(UUID id) implements Serializable {

        public JarIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
