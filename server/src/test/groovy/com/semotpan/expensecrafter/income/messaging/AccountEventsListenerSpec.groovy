package com.semotpan.expensecrafter.income.messaging

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.account.Account
import com.semotpan.expensecrafter.account.AccountCreated
import com.semotpan.expensecrafter.income.DefaultIncomeSources
import com.semotpan.expensecrafter.income.IncomeSource
import com.semotpan.expensecrafter.income.IncomeSources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Tag

import java.time.Duration

import static com.semotpan.expensecrafter.income.DataSamples.newSampleDefaultIncomeSources
import static java.util.UUID.randomUUID
import static org.awaitility.Awaitility.await
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.intersect
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestServerApplication)
@ApplicationModuleTest
@Import(Config)
class AccountEventsListenerSpec extends Specification {

    static account = new Account.AccountIdentifier(randomUUID())

    @Autowired
    Runnable eventProducer

    @Autowired
    IncomeSources incomeSources

    def "should create default income sources on account created event"() {
        when: 'account created event is published'
        eventProducer.run()

        def actual = []
        await().atMost(Duration.ofSeconds(5))
                .until {
                    actual = incomeSources.findByAccount(account)
                    !actual.isEmpty()
                }

        then: 'default income sources are created'
        def comp = { a, b -> a.name <=> b.name ?: a.account.id() <=> b.account.id() } as Comparator<? super IncomeSource>
        assert intersect(actual, newSampleDefaultIncomeSources(account), comp).size() == DefaultIncomeSources.values().size()

    }

    @TestConfiguration
    static class Config {

        @Bean
        Runnable eventProducer(ApplicationEventPublisher eventPublisher) {
            return new Runnable() {

                @Override
                @Transactional
                void run() {
                    eventPublisher.publishEvent(new AccountCreated(account))
                }
            }
        }
    }
}
