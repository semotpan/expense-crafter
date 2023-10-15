package com.semotpan.expensecrafter.expense.web;

import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.expense.ExpenseCommandRequest;
import com.semotpan.expensecrafter.expense.ExpenseService;
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
@RequestMapping(path = "/expenses")
@RequiredArgsConstructor
final class ExpenseController implements ExpenseControllerDoc {

    private final ExpenseService expenseService;
    private final ApiFailureHandler apiFailureHandler;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody ExpenseCommandRequest request) {
        return expenseService.createExpense(request)
                .fold(apiFailureHandler::handle, id -> created(fromCurrentRequest().path("/{id}").build(id)).build());
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ExpenseCommandRequest request) {
        return expenseService.updateExpense(new ExpenseIdentifier(id), request)
                .fold(apiFailureHandler::handle, expense -> noContent().build());
    }
}
