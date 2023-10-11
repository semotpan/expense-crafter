package com.semotpan.expensecrafter.account.web;

import com.semotpan.expensecrafter.account.Account;
import com.semotpan.expensecrafter.account.Accounts;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.semotpan.expensecrafter.account.Account.EmailAddress;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/accounts")
@RequiredArgsConstructor
final class AccountController implements AccountControllerDoc {

    private final Accounts accounts;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody AccountResource res) {
        var account = accounts.save(new Account(res.firstName(), res.lastName(), new EmailAddress(res.emailAddress())));

        return ResponseEntity.created(fromCurrentRequest().path("/{id}").build(account.getId())).build();
    }

    @Builder
    record AccountResource(UUID id,
                           String firstName,
                           String lastName,
                           String emailAddress) {
    }
}
