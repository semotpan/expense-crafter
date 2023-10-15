package com.semotpan.expensecrafter.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.io.Serializable;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Entity
@Table(name = "account")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE, force = true)
public final class Account extends AbstractAggregateRoot<Account> {

    static final String patternRFC5322 = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    static final int MAX_LENGTH = 256;

    private @EmbeddedId AccountIdentifier id;
    private @Embedded EmailAddress emailAddress;
    private String firstName;
    private String lastName;

    public Account(String firstName, String lastName, EmailAddress emailAddress) {
        this.id = new AccountIdentifier(UUID.randomUUID());
        this.emailAddress = requireNonNull(emailAddress, "emailAddress cannot be null");

        if (!StringUtils.isBlank(firstName)) {
            requireNonOverflow(firstName, "firstName overflow, max length allowed '%d'".formatted(MAX_LENGTH));
            this.firstName = firstName.trim();
        }

        if (!StringUtils.isBlank(lastName)) {
            requireNonOverflow(lastName, "lastName overflow, max length allowed '%d'".formatted(MAX_LENGTH));
            this.lastName = lastName.trim();
        }

        registerEvent(new AccountCreated(this.id));
    }

    @JsonCreator
    public static Account of(@JsonProperty("firstName") String firstName,
                             @JsonProperty("lastName") String lastName,
                             @JsonProperty("emailAddress") String emailAddress) {
        return new Account(firstName, lastName, new EmailAddress(emailAddress));
    }

    @Embeddable
    public record AccountIdentifier(UUID id) implements Serializable {

        public AccountIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    @Embeddable
    public record EmailAddress(String emailAddress) implements Serializable {

        public EmailAddress {
            if (isBlank(emailAddress)) {
                throw new IllegalArgumentException("emailAddress cannot be blank");
            }

            requireNonOverflow(emailAddress, "emailAddress max length must be '%d'".formatted(MAX_LENGTH));

            if (!compile(patternRFC5322).matcher(emailAddress).matches()) {
                throw new IllegalArgumentException("emailAddress must match '%s'".formatted(patternRFC5322));
            }
        }

        @Override
        public String toString() {
            return emailAddress;
        }
    }

    private static void requireNonOverflow(String text, String message) {
        if (StringUtils.length(text) > Account.MAX_LENGTH)
            throw new IllegalArgumentException(message);
    }
}
