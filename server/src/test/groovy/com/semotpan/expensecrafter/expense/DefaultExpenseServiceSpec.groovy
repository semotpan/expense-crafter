package com.semotpan.expensecrafter.expense

import com.semotpan.expensecrafter.account.Account
import com.semotpan.expensecrafter.shared.Failure
import spock.lang.Specification
import spock.lang.Tag

import static com.semotpan.expensecrafter.expense.DataSamples.*
import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asCollection
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.intersect

@Tag("unit")
class DefaultExpenseServiceSpec extends Specification {

    Categories categories
    Expenses expenses
    DefaultExpenseService service

    def setup() {
        categories = Mock()
        expenses = Mock()
        service = new DefaultExpenseService(categories, expenses)
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

    def "should create an expense"() {
        setup: 'repository mock behavior and interaction'
        var category = newSampleCategory()

        1 * categories.existsByIdAndAccount(_ as Category.CategoryIdentifier, _ as Account.AccountIdentifier) >> TRUE
        1 * categories.getReferenceById(_ as Category.CategoryIdentifier) >> category
        1 * expenses.save({ Expense e ->
            e == newSampleExpense(
                    id: [id: e.getId().id()],
                    creationTimestamp: e.getCreationTimestamp().toString()
            )
        }) >> newSampleExpense()

        when: 'new expense is created'
        def either = service.createExpense(newSampleExpenseCommandRequest())

        then: 'expense is persisted with new ID'
        assert either.isRight()

        and: 'expenseId is available'
        assert either.get() instanceof Expense.ExpenseIdentifier
    }

    def "should fail expense creation when category is not found"() {
        setup: 'repository mock behavior'
        1 * categories.existsByIdAndAccount(_ as Category.CategoryIdentifier, _ as Account.AccountIdentifier) >> FALSE

        when: 'expense fails to create'
        def either = service.createExpense(newSampleExpenseCommandRequest())

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided category is available'
        assert either.getLeft() == Failure.ofNotFound('Category or Account not found')
    }

    def "should fail expense creation when accountId is null"() {
        given: 'new command with null accountId'
        def command = newSampleExpenseCommandRequest(accountId: null)

        when: 'expense fails to create'
        def either = service.createExpense(command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on accountId field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense create request', [
                Failure.FieldViolation.builder()
                        .field('accountId')
                        .message('AccountId cannot be null')
                        .build()
        ])
    }

    def "should fail expense creation when categoryId is null"() {
        given: 'new command with null categoryId'
        def command = newSampleExpenseCommandRequest(categoryId: null)

        when: 'expense fails to create'
        def either = service.createExpense(command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on categoryId field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense create request', [
                Failure.FieldViolation.builder()
                        .field('categoryId')
                        .message('CategoryId cannot be null')
                        .build()
        ])
    }

    def "should fail expense creation when paymentType is invalid"() {
        given: 'new command with invalid payment type'
        def command = newSampleExpenseCommandRequest(paymentType: paymentType)

        when: 'expense fails to create'
        def either = service.createExpense(command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on paymentType field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense create request', [
                Failure.FieldViolation.builder()
                        .field('paymentType')
                        .message(failMessage)
                        .rejectedValue(paymentType)
                        .build()
        ])

        where:
        paymentType | failMessage
        '  '        | "PaymentType must be 'Cash' or 'Card'"
        'Hola'      | "PaymentType must be 'Cash' or 'Card'"
    }

    def "should fail expense creation when amount is invalid"() {
        given: 'new command with invalid amount'
        def command = newSampleExpenseCommandRequest(amount: amount)

        when: 'expense fails to create'
        def either = service.createExpense(command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on amount field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense create request', [
                Failure.FieldViolation.builder()
                        .field('amount')
                        .message(failMessage)
                        .rejectedValue(amount)
                        .build()
        ])

        where:
        amount | failMessage
        null   | 'Amount cannot be null'
        0.0    | 'Amount must be positive value'
        -25.56 | 'Amount must be positive value'
    }

    def "should fail expense creation when expenseDate is null"() {
        given: 'new command with null expenseDate'
        def command = newSampleExpenseCommandRequest(expenseDate: null)

        when: 'expense fails to create'
        def either = service.createExpense(command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on expenseDate field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense create request', [
                Failure.FieldViolation.builder()
                        .field('expenseDate')
                        .message('ExpenseDate cannot be null')
                        .build()
        ])
    }
}
