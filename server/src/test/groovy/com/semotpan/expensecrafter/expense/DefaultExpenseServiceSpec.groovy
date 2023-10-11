package com.semotpan.expensecrafter.expense

import com.semotpan.expensecrafter.account.Account
import spock.lang.Specification
import spock.lang.Tag

import static com.semotpan.expensecrafter.expense.DataSamples.newSampleDefaultCategories
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asCollection
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.intersect

@Tag("unit")
class DefaultExpenseServiceSpec extends Specification {

    Categories categories
    DefaultExpenseService service

    def setup() {
        categories = Mock()
        service = new DefaultExpenseService(categories)
    }

    def "should create default categories"() {
        def comp = { a, b -> a.name <=> b.name ?: a.account.id() <=> b.account.id() } as Comparator<? super Category>

        setup: 'repository mock behavior and interaction'
        def account = new Account.AccountIdentifier(UUID.randomUUID())
        1 * categories.saveAll { actual ->
            intersect(asCollection(actual), newSampleDefaultCategories(account), comp).size() == DefaultCategories.values().size()
        } >> []

        expect: 'created default categories for provided accountId'
        service.initDefaultCategories(account)
    }
}
