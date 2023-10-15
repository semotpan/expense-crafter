package com.semotpan.expensecrafter.expense.web;

import com.semotpan.expensecrafter.expense.ExpenseCommandRequest;
import com.semotpan.expensecrafter.expense.ExpenseService;
import com.semotpan.expensecrafter.shared.ApiFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
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
}
