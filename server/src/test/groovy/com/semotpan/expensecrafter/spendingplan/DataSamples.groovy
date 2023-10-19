package com.semotpan.expensecrafter.spendingplan

import com.fasterxml.jackson.databind.json.JsonMapper
import groovy.json.JsonOutput

import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand

class DataSamples {

    static MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build()

    static PLAN_CREATE_COMMAND = [
            accountId  : "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca",
            name       : "My spending plan",
            amount     : 1000,
            description: "My basic spending plan"
    ]

    static AMOUNT = [
            amount  : 1000.0,
            currency: "EUR"
    ]

    static PLAN = [
            id               : [id: "3b257779-a5db-4e87-9365-72c6f8d4977d"],
            account          : [id: "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca"],
            creationTimestamp: "2023-10-10T18:28:04.224870Z",
            amount           : AMOUNT,
            name             : "My spending plan",
            description      : "My basic spending plan"
    ]

    static newSamplePlanCreateCommand(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(PLAN_CREATE_COMMAND + map) as String, PlanCreateCommand.class)
    }

    static newSamplePlan(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(PLAN + map) as String, Plan.class)
    }
}
