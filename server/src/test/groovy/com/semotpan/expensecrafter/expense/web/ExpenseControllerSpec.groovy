package com.semotpan.expensecrafter.expense.web

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.expense.DataSamples
import com.semotpan.expensecrafter.expense.ExpenseCreated
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.PublishedEvents
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification
import spock.lang.Tag

import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import static org.springframework.http.MediaType.APPLICATION_JSON

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestServerApplication)
@ApplicationModuleTest
class ExpenseControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PublishedEvents events

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

    def postNewExpense(String req) {
        restTemplate.postForEntity('/expenses', entityRequest(req), String.class)
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

    def expectedCreationFailure() {
        def filePath = 'expense/expense-creation-failure-response.json'
        def failureAsMap = new JsonSlurper().parse(new ClassPathResource(filePath).getFile())
        JsonOutput.toJson(failureAsMap)
    }
}
