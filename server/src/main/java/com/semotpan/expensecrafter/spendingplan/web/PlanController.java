package com.semotpan.expensecrafter.spendingplan.web;

import com.semotpan.expensecrafter.shared.ApiFailureHandler;
import com.semotpan.expensecrafter.spendingplan.DefaultPlan;
import com.semotpan.expensecrafter.spendingplan.PlanService;
import com.semotpan.expensecrafter.spendingplan.PlanService.JarCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand;
import static com.semotpan.expensecrafter.spendingplan.web.PlanControllerDoc.PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = PATH)
@RequiredArgsConstructor
final class PlanController implements PlanControllerDoc {

    private final PlanService planService;
    private final ApiFailureHandler apiFailureHandler;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody PlanCreateCommand command) {
        return planService.createPlan(command)
                .fold(apiFailureHandler::handle, id -> created(fromCurrentRequest().path("/{id}").build(id)).build());
    }

    @PostMapping(path = "/default", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDefault(@RequestBody DefaultPlanRequest req) {
        var jars = Arrays.stream(DefaultPlan.values())
                .map(c -> new JarCreateCommand(c.getName(), c.getPercentage(), c.getDescription()))
                .toList();

        var defaultPlanCommand = PlanCreateCommand.builder()
                .accountId(req.accountId())
                .amount(req.amount())
                .name(DefaultPlan.NAME)
                .description(DefaultPlan.DESCRIPTION)
                .jars(jars)
                .build();

        return planService.createPlan(defaultPlanCommand)
                .fold(apiFailureHandler::handle, id -> created(fromCurrentContextPath().path("/{path}/{id}").build(PATH, id)).build());
    }

    record DefaultPlanRequest(BigDecimal amount, UUID accountId) {}
}
