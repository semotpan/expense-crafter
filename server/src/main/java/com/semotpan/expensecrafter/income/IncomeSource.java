package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Entity
@Table(name = "income_source")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE, force = true)
public class IncomeSource {

    static final int MAX_LENGTH = 100;

    @EmbeddedId
    private IncomeSourceIdentifier id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private AccountIdentifier account;

    private String name;

    public IncomeSource(String name, AccountIdentifier account) {
        this.id = new IncomeSource.IncomeSourceIdentifier(UUID.randomUUID());
        this.account = requireNonNull(account, "account cannot be null");
        this.name = requireValidName(name);
    }

    private String requireValidName(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (length(name) > MAX_LENGTH) {
            throw new IllegalArgumentException("name overflow, max length allowed '%d'".formatted(MAX_LENGTH));
        }

        return name;
    }

    @Embeddable
    public record IncomeSourceIdentifier(UUID id) implements Serializable {

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
