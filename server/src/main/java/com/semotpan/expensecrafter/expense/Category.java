package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
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
@Table(name = "expense_category")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE, force = true)
public final class Category {

    static final int MAX_LENGTH = 100;

    @EmbeddedId
    private final CategoryIdentifier id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private final Account.AccountIdentifier account;

    private String name;

    public Category(String name, Account.AccountIdentifier account) {
        this.id = new CategoryIdentifier(UUID.randomUUID());
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
    public record CategoryIdentifier(UUID id) implements Serializable {

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
