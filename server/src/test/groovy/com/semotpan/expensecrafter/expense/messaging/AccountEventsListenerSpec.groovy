package com.semotpan.expensecrafter.expense.messaging

import com.semotpan.expensecrafter.TestServerApplication
import com.semotpan.expensecrafter.account.Account
import com.semotpan.expensecrafter.account.AccountCreated
import com.semotpan.expensecrafter.expense.Categories
import com.semotpan.expensecrafter.expense.Category
import com.semotpan.expensecrafter.expense.DefaultCategories
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

import static com.semotpan.expensecrafter.expense.DataSamples.newSampleDefaultCategories
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
    Categories categories

    @Autowired
    Runnable eventProducer

    def "should create default expense categories on account created event"() {
        when: 'account created event is published'
        eventProducer.run()

        def actual = []
        await().atMost(Duration.ofSeconds(5))
                .until {
                    actual = categories.findByAccount(account)
                    !actual.isEmpty()
                }

        then: 'default expense categories are created'
        def comp = { a, b -> a.name <=> b.name ?: a.account.id() <=> b.account.id() } as Comparator<? super Category>
        assert intersect(actual, newSampleDefaultCategories(account), comp).size() == DefaultCategories.values().size()
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
