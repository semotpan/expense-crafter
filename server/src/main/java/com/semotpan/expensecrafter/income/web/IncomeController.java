package com.semotpan.expensecrafter.income.web;

import com.semotpan.expensecrafter.income.IncomeCommand;
import com.semotpan.expensecrafter.income.IncomeService;
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
}
