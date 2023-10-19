package com.semotpan.expensecrafter.spendingplan.web;

import com.semotpan.expensecrafter.spendingplan.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpHeaders.LOCATION;

public interface PlanControllerDoc {

    String TAG = "spending-plans";

    @Operation(summary = "Add a new spending plan in the Expense Crafter",
            description = "Operation to add a new spending plan for the current logged-in account, the plan must have an unique name and positive plan amount",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spending plan created successfully", headers = @Header(name = LOCATION)),
            @ApiResponse(responseCode = "400", description = "Invalid JSON request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content),
            @ApiResponse(responseCode = "409", description = "Spending plan name already exists", content = @Content),
            @ApiResponse(responseCode = "422", description = "Field validation failures", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<?> create(@RequestBody(description = "Spending Plan Resource to be created", required = true) PlanService.PlanCreateCommand command);

}
