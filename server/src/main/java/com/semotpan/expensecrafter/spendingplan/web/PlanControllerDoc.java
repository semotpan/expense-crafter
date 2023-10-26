package com.semotpan.expensecrafter.spendingplan.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand;
import static com.semotpan.expensecrafter.spendingplan.web.PlanController.DefaultPlanRequest;
import static org.springframework.http.HttpHeaders.LOCATION;

public interface PlanControllerDoc {

    String PATH = "spending-plans";

    @Operation(summary = "Add a new spending plan in the Expense Crafter",
            description = "Operation to add a new spending plan for the current logged-in account, the plan must have an unique name and positive plan amount",
            security = {@SecurityRequirement(name = "openId")},
            tags = {PATH})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spending plan created successfully", headers = @Header(name = LOCATION)),
            @ApiResponse(responseCode = "400", description = "Invalid JSON request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content),
            @ApiResponse(responseCode = "409", description = "Spending plan name already exists", content = @Content),
            @ApiResponse(responseCode = "422", description = "Field validation failures", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<?> create(@RequestBody(description = "Spending Plan Resource to be created", required = true) PlanCreateCommand command);

    @Operation(summary = "Add a default spending plan in the Expense Crafter",
            description = "Operation to add a default spending plan for the current logged-in account, the plan must have an unique name and positive plan amount, " +
                    "default jar distribution: Necessities(55%), Long Term Savings(10%), Education(10%), Play(10%), Financial(10%), Give(5%).",
            security = {@SecurityRequirement(name = "openId")},
            tags = {PATH})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spending plan created successfully", headers = @Header(name = LOCATION)),
            @ApiResponse(responseCode = "400", description = "Invalid JSON request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content),
            @ApiResponse(responseCode = "409", description = "Spending plan name already exists", content = @Content),
            @ApiResponse(responseCode = "422", description = "Field validation failures", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<?> createDefault(@RequestBody(description = "Spending Plan Resource to be created", required = true) DefaultPlanRequest command);

}
