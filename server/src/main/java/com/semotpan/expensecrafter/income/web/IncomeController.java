package com.semotpan.expensecrafter.income.web;

import com.semotpan.expensecrafter.income.Income;
import com.semotpan.expensecrafter.income.IncomeCommand;
import com.semotpan.expensecrafter.income.IncomeService;
import com.semotpan.expensecrafter.shared.ApiFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/incomes")
@RequiredArgsConstructor
final class IncomeController implements IncomeControllerDoc {

    private final IncomeService incomeService;
    private final ApiFailureHandler apiFailureHandler;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody IncomeCommand command) {
        return incomeService.createIncome(command)
                .fold(apiFailureHandler::handle, id -> created(fromCurrentRequest().path("/{id}").build(id)).build());
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody IncomeCommand command) {
        return incomeService.updateIncome(new Income.IncomeIdentifier(id), command)
                .fold(apiFailureHandler::handle, income -> noContent().build());

    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return incomeService.deleteIncome(new Income.IncomeIdentifier(id))
                .fold(apiFailureHandler::handle, income -> noContent().build());

    }
}
