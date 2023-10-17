package com.semotpan.expensecrafter.income.web

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.income.DataSamples
import com.semotpan.expensecrafter.income.IncomeCreated
import com.semotpan.expensecrafter.income.IncomeUpdated
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
import static org.springframework.http.HttpMethod.PUT
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.MediaType.APPLICATION_JSON

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestServerApplication)
@ApplicationModuleTest
class IncomeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PublishedEvents events

    @Autowired
    JdbcTemplate jdbcTemplate

    def cleanup() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, 'income', 'income_source')
    }

    @Sql('/income/create-income-source.sql')
    def "should create a new income"() {
        given: 'user wants to create a new income'
        var request = newValidCreateRequest()

        when: 'income is created'
        var response = postNewIncome(request)

        then: 'response status is created'
        assert response.getStatusCode() == CREATED

        and: 'location header contains the created income URL location'
        assert response.getHeaders().getLocation() != null

        and: 'income created event raised'
        assert events.ofType(IncomeCreated.class).size() == 1
    }

    def "should fail creation when request has validation failures"() {
        given: 'user wants to create a new income'
        var request = newInvalidRequest()

        when: 'income fails to create'
        var response = postNewIncome(request)

        then: 'response has status code unprocessable entity'
        assert response.getStatusCode() == UNPROCESSABLE_ENTITY

        and: 'response body contains validation failure response'
        JSONAssert.assertEquals(expectedCreationFailure(), response.getBody(), LENIENT)
    }

    @Sql(['/income/create-income-source.sql', '/income/create-income.sql'])
    def "should update an income"() {
        given: 'user wants to update an income'
        var request = newValidUpdateRequest()

        when: 'income is updated'
        var response = putAnIncome(request)

        then: 'response status is no content'
        assert response.getStatusCode() == NO_CONTENT

        and: 'income updated event raised'
        assert events.ofType(IncomeUpdated.class).size() == 1
    }

    def "should fail update when income source or account not found"() {
        given: 'user wants to update an income'
        var request = newValidUpdateRequest()

        when: 'expense fails to update'
        var response = putAnIncome(request)

        then: 'response has status code not found'
        assert response.getStatusCode() == NOT_FOUND

        and: 'response body contains not found failure response'
        JSONAssert.assertEquals(expectedUpdateFailure(), response.getBody(), LENIENT)
    }

    def putAnIncome(String req) {
        restTemplate.exchange(
                '/incomes/3b257779-a5db-4e87-9365-72c6f8d4977d',
                PUT,
                entityRequest(req),
                String.class
        )
    }

    def postNewIncome(String req) {
        restTemplate.postForEntity('/incomes', entityRequest(req), String.class)
    }

    def entityRequest(String req) {
        var headers = new HttpHeaders()
        headers.setContentType(APPLICATION_JSON)
        new HttpEntity<>(req, headers)
    }

    def newValidCreateRequest() {
        JsonOutput.toJson(DataSamples.INCOME_COMMAND)
    }

    def newInvalidRequest() {
        JsonOutput.toJson([
                paymentType: 'Other'
        ])
    }

    def newValidUpdateRequest() {
        JsonOutput.toJson(DataSamples.INCOME_COMMAND + [
                incomeSourceId: 'e2709aa2-7907-4f78-98b6-0f36a0c1b5ca',
                paymentType   : "Card",
                amount        : 50,
                expenseDate   : '2023-10-17',
                description   : 'Profit'
        ])
    }

    def expectedCreationFailure() {
        def filePath = 'income/income-creation-failure-response.json'
        def failureAsMap = new JsonSlurper().parse(new ClassPathResource(filePath).getFile())
        JsonOutput.toJson(failureAsMap)
    }

    def expectedUpdateFailure() {
        JsonOutput.toJson([
                status   : 404,
                errorCode: "NOT_FOUND",
                message  : "Income Source or Account not found"
        ])
    }
}
