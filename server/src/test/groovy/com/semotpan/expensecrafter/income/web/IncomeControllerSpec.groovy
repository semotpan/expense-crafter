package com.semotpan.expensecrafter.income.web

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.income.DataSamples
import com.semotpan.expensecrafter.income.IncomeCreated
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
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
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

    def expectedCreationFailure() {
        def filePath = 'income/income-creation-failure-response.json'
        def failureAsMap = new JsonSlurper().parse(new ClassPathResource(filePath).getFile())
        JsonOutput.toJson(failureAsMap)
    }
}
