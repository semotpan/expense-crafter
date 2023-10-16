package com.semotpan.expensecrafter.expense


import com.semotpan.expensecrafter.shared.Failure
import spock.lang.Specification
import spock.lang.Tag

import static com.semotpan.expensecrafter.account.Account.AccountIdentifier
import static com.semotpan.expensecrafter.expense.Category.CategoryIdentifier
import static com.semotpan.expensecrafter.expense.DataSamples.*
import static com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier
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
        def account = new AccountIdentifier(UUID.randomUUID())
        1 * categories.saveAll { actual ->
            intersect(asCollection(actual), newSampleDefaultCategories(account), comp).size() == DefaultCategories.values().size()
        } >> []

        expect: 'created default categories for provided accountId'
        service.initDefaultCategories(account)
    }

    def "should create an expense"() {
        setup: 'repository mock behavior and interaction'
        1 * categories.existsByIdAndAccount(_ as CategoryIdentifier, _ as AccountIdentifier) >> TRUE
        1 * categories.getReferenceById(_ as CategoryIdentifier) >> newSampleCategory()
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
        assert either.get() instanceof ExpenseIdentifier
    }

    def "should fail expense creation when category is not found"() {
        setup: 'repository mock behavior'
        1 * categories.existsByIdAndAccount(_ as CategoryIdentifier, _ as AccountIdentifier) >> FALSE

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

    def "should update an expense"() {
        setup: 'repository mock behavior and interaction'
        1 * categories.existsByIdAndAccount(_ as CategoryIdentifier, _ as AccountIdentifier) >> TRUE
        1 * expenses.findById(_ as ExpenseIdentifier) >> Optional.of(newSampleExpense())
        1 * categories.getReferenceById(_ as CategoryIdentifier) >> newSampleCategory(id: [id: "2298dfbc-4eb3-4d83-95f4-dd7a56d21136"])
        1 * expenses.save(_ as Expense) >> newSampleExpense()

        def command = newSampleExpenseCommandRequest([
                categoryId : "2298dfbc-4eb3-4d83-95f4-dd7a56d21136",
                amount     : 50,
                paymentType: "Card",
                expenseDate: "2023-10-15",
                description: "Pencils buying"
        ])

        when: 'expense is updated'
        def uuid = UUID.fromString('3b257779-a5db-4e87-9365-72c6f8d4977d')
        def either = service.updateExpense(new ExpenseIdentifier(uuid), command)

        then: 'updated expense is present'
        assert either.isRight()

        and: 'expense values match command request'
        assert either.get() == newSampleExpense([
                category   : newSampleCategory(id: [id: "2298dfbc-4eb3-4d83-95f4-dd7a56d21136"]),
                amount     : AMOUNT + [amount: 50.00],
                paymentType: "CARD",
                expenseDate: "2023-10-15",
                description: "Pencils buying"
        ])
    }

    def "should fail expense update when accountId is null"() {
        given: 'new command with null accountId'
        def command = newSampleExpenseCommandRequest(accountId: null)

        when: 'expense fails to create'
        def either = service.updateExpense(new ExpenseIdentifier(UUID.randomUUID()), command)

        then: 'validation failure result is present'
        assert either.isLeft()

        and: 'validation failure on accountId field'
        assert either.getLeft() == Failure.ofValidation('Failures on expense update request', [
                Failure.FieldViolation.builder()
                        .field('accountId')
                        .message('AccountId cannot be null')
                        .build()
        ])
    }

    def "should fail expense update when category or account not found"() {
        setup: 'repository mock behavior and interaction'
        1 * categories.existsByIdAndAccount(_ as CategoryIdentifier, _ as AccountIdentifier) >> FALSE

        when: 'expense fails to update'
        def either = service.updateExpense(new ExpenseIdentifier(UUID.randomUUID()), newSampleExpenseCommandRequest())

        then: 'not found failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided category and account'
        assert either.getLeft() == Failure.ofNotFound('Category or Account not found')
    }

    def "should fail expense update when expense not found"() {
        setup: 'repository mock behavior and interaction'
        1 * categories.existsByIdAndAccount(_ as CategoryIdentifier, _ as AccountIdentifier) >> TRUE
        1 * expenses.findById(_ as ExpenseIdentifier) >> Optional.empty()

        when: 'expense fails to update'
        def either = service.updateExpense(new ExpenseIdentifier(UUID.randomUUID()), newSampleExpenseCommandRequest())

        then: 'failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided expenseId'
        assert either.getLeft() == Failure.ofNotFound('Expense not found')
    }

    def "should delete an expense"() {
        setup: 'repository mock behavior and interaction'
        def expense = newSampleExpense()
        1 * expenses.findById(_ as ExpenseIdentifier) >> Optional.of(expense)

        when: 'expense is deleted'
        def either = service.deleteExpense(expense.getId())

        then: 'no result is present'
        assert either.isRight()

        and: 'expenses repository invoked'
        1 * expenses.delete(expense)
    }

    def "should fail delete when expense not found"() {
        setup: 'repository mock behavior and interaction'
        1 * expenses.findById(_ as ExpenseIdentifier) >> Optional.empty()

        when: 'expense failed to delete'
        def either = service.deleteExpense(new ExpenseIdentifier(UUID.randomUUID()))

        then: 'failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided expenseId'
        assert either.getLeft() == Failure.ofNotFound('Expense not found')
    }
}
