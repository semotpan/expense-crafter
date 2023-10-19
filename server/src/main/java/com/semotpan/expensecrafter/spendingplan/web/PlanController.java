package com.semotpan.expensecrafter.spendingplan.web;

import com.semotpan.expensecrafter.shared.ApiFailureHandler;
import com.semotpan.expensecrafter.spendingplan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/spending-plans")
@RequiredArgsConstructor
final class PlanController implements PlanControllerDoc {

    private final PlanService planService;
    private final ApiFailureHandler apiFailureHandler;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody PlanCreateCommand command) {
        return planService.createPlan(command)
                .fold(apiFailureHandler::handle, id -> created(fromCurrentRequest().path("/{id}").build(id)).build());
    }
}
