package com.semotpan.expensecrafter.spendingplan.web

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.spendingplan.DataSamples
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
class PlanControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    JdbcTemplate jdbcTemplate

    def cleanup() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, 'spending_plan')
    }

    def "should create a spending plan"() {
        given: 'user wants to create a new spending plan'
        var req = newValidCreateRequest()

        when: 'spending plan is created'
        var res = postNewPlan(req)

        then: 'response status is created'
        assert res.getStatusCode() == CREATED

        and: 'location header contains the created spending plan URL location'
        assert res.getHeaders().getLocation() != null
    }

    def "should fail creation when request has validation failures"() {
        given: 'user wants to create a new spending plan'
        var req = newInvalidRequest()

        when: 'spending plan fails to create'
        var res = postNewPlan(req)

        then: 'response has status code unprocessable entity'
        assert res.getStatusCode() == UNPROCESSABLE_ENTITY

        and: 'response body contains validation failure response'
        JSONAssert.assertEquals(expectedCreationFailure(), res.getBody(), LENIENT)
    }

    def postNewPlan(String req) {
        restTemplate.postForEntity('/spending-plans', entityRequest(req), String.class)
    }

    def entityRequest(String req) {
        var headers = new HttpHeaders()
        headers.setContentType(APPLICATION_JSON)
        new HttpEntity<>(req, headers)
    }

    def newValidCreateRequest() {
        JsonOutput.toJson(DataSamples.PLAN_CREATE_COMMAND)
    }

    def newInvalidRequest() {
        JsonOutput.toJson([
                amount     : -10
        ])
    }

    def expectedCreationFailure() {
        def filePath = 'spending-plan/plan-creation-failure-response.json'
        def failureAsMap = new JsonSlurper().parse(new ClassPathResource(filePath).getFile())
        JsonOutput.toJson(failureAsMap)
    }
}
