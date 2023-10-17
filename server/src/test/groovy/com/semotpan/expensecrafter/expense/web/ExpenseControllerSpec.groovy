package com.semotpan.expensecrafter.expense.web

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.expense.DataSamples
import com.semotpan.expensecrafter.expense.ExpenseCreated
import com.semotpan.expensecrafter.expense.ExpenseDeleted
import com.semotpan.expensecrafter.expense.ExpenseUpdated
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.PublishedEvents
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.jdbc.JdbcTestUtils
import spock.lang.Specification
import spock.lang.Tag

import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.PUT
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.MediaType.APPLICATION_JSON

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestServerApplication)
@ApplicationModuleTest
class ExpenseControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PublishedEvents events

    @Autowired
    JdbcTemplate jdbcTemplate

    def cleanup() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, 'expense', 'expense_category')
    }

    @Sql('/expense/create-expense-category.sql')
    def "should create a new expense"() {
        given: 'user wants to create a new expense'
        var request = newValidCreateRequest()

        when: 'expense is created'
        var response = postNewExpense(request)

        then: 'response status is created'
        assert response.getStatusCode() == CREATED

        and: 'location header contains the created expense URL location'
        assert response.getHeaders().getLocation() != null

        and: 'expense created event raised'
        assert events.ofType(ExpenseCreated.class).size() == 1
    }

    def "should fail creation when request has validation failures"() {
        given: 'user wants to create a new expense'
        var request = newInvalidRequest()

        when: 'expense fails to create'
        var response = postNewExpense(request)

        then: 'response has status code unprocessable entity'
        assert response.getStatusCode() == UNPROCESSABLE_ENTITY

        and: 'response body contains validation failure response'
        JSONAssert.assertEquals(expectedCreationFailure(), response.getBody(), LENIENT)
    }

    @Sql(['/expense/create-expense-category.sql', '/expense/create-expense.sql'])
    def "should update an expense"() {
        given: 'user wants to update an expense'
        var request = newValidUpdateRequest()

        when: 'expense is updated'
        var response = putAnExpense(request)

        then: 'response status is no content'
        assert response.getStatusCode() == NO_CONTENT

        and: 'expense updated event raised'
        assert events.ofType(ExpenseUpdated.class).size() == 1
    }

    def "should fail update when category or account not found"() {
        given: 'user wants to update an expense'
        var request = newValidUpdateRequest()

        when: 'expense fails to update'
        var response = putAnExpense(request)

        then: 'response has status code not found'
        assert response.getStatusCode() == NOT_FOUND

        and: 'response body contains not found failure response'
        JSONAssert.assertEquals(expectedUpdateFailure(), response.getBody(), LENIENT)
    }

    @Sql(['/expense/create-expense-category.sql', '/expense/create-expense.sql'])
    def "should delete an expense"() {
        given: 'user wants to delete an expense'
        var expenseId = UUID.fromString('3b257779-a5db-4e87-9365-72c6f8d4977d')

        when: 'expense is deleted'
        var response = deleteAnExpense(expenseId)

        then: 'response status is no content'
        assert response.getStatusCode() == NO_CONTENT

        and: 'expense deleted event raised'
        assert events.ofType(ExpenseDeleted.class).size() == 1
    }

    def "should fail delete when expense not found"() {
        given: 'user wants to delete an expense'
        var expenseId = UUID.randomUUID()

        when: 'expense fails to delete'
        var response = deleteAnExpense(expenseId)

        then: 'response has status code not found'
        assert response.getStatusCode() == NOT_FOUND

        and: 'response body contains not found failure response'
        JSONAssert.assertEquals(expectedDeleteFailure(), response.getBody(), LENIENT)
    }

    def postNewExpense(String req) {
        restTemplate.postForEntity('/expenses', entityRequest(req), String.class)
    }

    def putAnExpense(String req) {
        restTemplate.exchange(
                '/expenses/3b257779-a5db-4e87-9365-72c6f8d4977d',
                PUT,
                entityRequest(req),
                String.class
        )
    }

    def deleteAnExpense(UUID expenseId) {
        restTemplate.exchange(
                "/expenses/${expenseId}",
                DELETE,
                entityRequest(null),
                String.class
        )
    }

    def entityRequest(String req) {
        var headers = new HttpHeaders()
        headers.setContentType(APPLICATION_JSON)
        new HttpEntity<>(req, headers)
    }

    def newValidCreateRequest() {
        JsonOutput.toJson(DataSamples.EXPENSE_COMMAND_REQUEST)
    }

    def newInvalidRequest() {
        JsonOutput.toJson([
                paymentType: 'Other'
        ])
    }

    def newValidUpdateRequest() {
        JsonOutput.toJson(DataSamples.EXPENSE_COMMAND_REQUEST + [
                categoryId : 'e2709aa2-7907-4f78-98b6-0f36a0c1b5ca',
                paymentType: "Card",
                amount     : 50,
                expenseDate: '2023-10-15',
                description: 'Pencils buying'
        ])
    }

    def expectedCreationFailure() {
        def filePath = 'expense/expense-creation-failure-response.json'
        def failureAsMap = new JsonSlurper().parse(new ClassPathResource(filePath).getFile())
        JsonOutput.toJson(failureAsMap)
    }

    def expectedUpdateFailure() {
        JsonOutput.toJson([
                status   : 404,
                errorCode: "NOT_FOUND",
                message  : "Category or Account not found"
        ])
    }

    def expectedDeleteFailure() {
        JsonOutput.toJson([
                status   : 404,
                errorCode: "NOT_FOUND",
                message  : "Expense not found"
        ])
    }
}
