package com.semotpan.expensecrafter.expense.web;

import com.semotpan.expensecrafter.expense.ExpenseCommandRequest;
import com.semotpan.expensecrafter.shared.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface ExpenseControllerDoc {

    String TAG = "expenses";

    @Operation(summary = "Add a new expense in the Expense Crafter",
            description = "Operation to add a new expense for the current logged-in account, the expense must be attached to an expense category",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation", headers = @Header(name = LOCATION)),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation Failure", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<?> create(@RequestBody(description = "Expense Resource to be created", required = true) ExpenseCommandRequest request);

    @Operation(summary = "Full-update an expense in the Expense Crafter",
            description = "Operation to full-update an existing expense for the current logged-in account, the fields allowed to be updated: amount, paymentType, expenseDate, category, description!",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation Failure", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<?> update(@Parameter(in = ParameterIn.PATH, description = "ExpenseId to be updated", required = true) UUID expenseId,
                             @RequestBody(description = "Expense Resource to be updated", required = true) ExpenseCommandRequest request);

    @Operation(summary = "Delete an expense in the Expense Crafter",
            description = "Operation to delete an existing expense for the current logged-in account.",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found Failure", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<?> delete(@Parameter(in = ParameterIn.PATH, description = "ExpenseId to be deleted", required = true) UUID id);

}
