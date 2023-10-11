package com.semotpan.expensecrafter.account.web

import com.semotpan.expensecrafter.TestServerApplication
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.modulith.test.ApplicationModuleTest
import spock.lang.Specification
import spock.lang.Tag

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.MediaType.APPLICATION_JSON

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestServerApplication)
@ApplicationModuleTest
class AccountControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    def "should create an new account"() {
        given: 'user wants to create an account'
        var request = JsonOutput.toJson([
                firstName   : 'Jon',
                lastName    : 'Snow',
                emailAddress: 'jonsnow@gmail.com'
        ])

        when: 'account is created'
        var response = postNewAccount(request)

        then: 'response has status code created'
        assert response.getStatusCode() == CREATED

        and: 'location header contains the create account URL location'
        assert response.getHeaders().getLocation() != null
    }

    def postNewAccount(String req) {
        var headers = new HttpHeaders()
        headers.setContentType(APPLICATION_JSON)

        restTemplate.postForEntity('/accounts', new HttpEntity<>(req, headers), String.class)
    }
}
