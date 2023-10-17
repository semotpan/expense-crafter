package com.semotpan.expensecrafter.income

import com.semotpan.expensecrafter.shared.Failure
import spock.lang.Specification
import spock.lang.Tag

import static com.semotpan.expensecrafter.account.Account.AccountIdentifier
import static com.semotpan.expensecrafter.income.DataSamples.*
import static com.semotpan.expensecrafter.income.Income.IncomeIdentifier
import static com.semotpan.expensecrafter.income.IncomeSource.IncomeSourceIdentifier
import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE
import static java.util.UUID.fromString
import static java.util.UUID.randomUUID

@Tag("unit")
class DefaultIncomeServiceSpec extends Specification {

    IncomeSources incomeSources
    Incomes incomes
    DefaultIncomeService service

    def setup() {
        incomeSources = Mock()
        incomes = Mock()
        service = new DefaultIncomeService(incomeSources, incomes)
    }

    def "should create an income"() {
        setup: 'repository mock behavior and interaction'
        1 * incomeSources.existsByIdAndAccount(_ as IncomeSourceIdentifier, _ as AccountIdentifier) >> TRUE
        1 * incomeSources.getReferenceById(_ as IncomeSourceIdentifier) >> newSampleIncomeSource()
        1 * incomes.save({ Income i ->
            i == newSampleIncome([
                    id               : [id: i.getId().id()],
                    creationTimestamp: i.getCreationTimestamp().toString()
            ])
        }) >> newSampleIncome()

        when: 'new income is created'
        def either = service.createIncome(newSampleIncomeCommand())

        then: 'income is persisted with new ID'
        assert either.isRight()

        and: 'incomeId is available'
        assert either.get() instanceof IncomeIdentifier
    }

    def "should fail income creation when accountId is null"() {
        given: 'new command with null accountId'
        def command = newSampleIncomeCommand(accountId: null)

        when: 'income fails to create'
        def either = service.createIncome(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on accountId field'
        assert either.getLeft() == Failure.ofValidation('Failures on income create request', [
                Failure.FieldViolation.builder()
                        .field('accountId')
                        .message('AccountId cannot be null')
                        .build()
        ])
    }

    def "should fail income creation when incomeSourceId is null"() {
        given: 'new command with null incomeSourceId'
        def command = newSampleIncomeCommand(incomeSourceId: null)

        when: 'income fails to create'
        def either = service.createIncome(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on incomeSourceId field'
        assert either.getLeft() == Failure.ofValidation('Failures on income create request', [
                Failure.FieldViolation.builder()
                        .field('incomeSourceId')
                        .message('IncomeSourceId cannot be null')
                        .build()
        ])
    }

    def "should fail income creation when paymentType is invalid"() {
        given: 'new command with invalid payment type'
        def command = newSampleIncomeCommand(paymentType: paymentType)

        when: 'income fails to create'
        def either = service.createIncome(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on paymentType field'
        assert either.getLeft() == Failure.ofValidation('Failures on income create request', [
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

    def "should fail income creation when amount is invalid"() {
        given: 'new command with invalid amount'
        def command = newSampleIncomeCommand(amount: amount)

        when: 'income fails to create'
        def either = service.createIncome(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on amount field'
        assert either.getLeft() == Failure.ofValidation('Failures on income create request', [
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

    def "should fail income creation when incomeDate is null"() {
        given: 'new command with null incomeDate'
        def command = newSampleIncomeCommand(incomeDate: null)

        when: 'income fails to create'
        def either = service.createIncome(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on incomeDate field'
        assert either.getLeft() == Failure.ofValidation('Failures on income create request', [
                Failure.FieldViolation.builder()
                        .field('incomeDate')
                        .message('IncomeDate cannot be null')
                        .build()
        ])
    }

    def "should fail income creation when income source or account not found"() {
        setup: 'repository mock behavior and interaction'
        1 * incomeSources.existsByIdAndAccount(_ as IncomeSourceIdentifier, _ as AccountIdentifier) >> FALSE

        when: 'income fails to create'
        def either = service.createIncome(newSampleIncomeCommand())

        then: 'failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided category is available'
        assert either.getLeft() == Failure.ofNotFound('Income Source or Account not found')
    }

    def "should fail income update when accountId is null"() {
        given: 'new command with null accountId'
        def command = newSampleIncomeCommand(accountId: null)

        when: 'income fails to update'
        def either = service.updateIncome(new IncomeIdentifier(randomUUID()), command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on accountId field'
        assert either.getLeft() == Failure.ofValidation('Failures on income update request', [
                Failure.FieldViolation.builder()
                        .field('accountId')
                        .message('AccountId cannot be null')
                        .build()
        ])
    }

    def "should fail income update when income source or account not found"() {
        setup: 'repository mock behavior and interaction'
        1 * incomeSources.existsByIdAndAccount(_ as IncomeSourceIdentifier, _ as AccountIdentifier) >> FALSE

        when: 'income fails to update'
        def either = service.updateIncome(new IncomeIdentifier(randomUUID()), newSampleIncomeCommand())

        then: 'not found failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided income source and account'
        assert either.getLeft() == Failure.ofNotFound('Income Source or Account not found')
    }

    def "should fail income update when income not found"() {
        setup: 'repository mock behavior and interaction'
        1 * incomeSources.existsByIdAndAccount(_ as IncomeSourceIdentifier, _ as AccountIdentifier) >> TRUE
        1 * incomes.findById(_ as IncomeIdentifier) >> Optional.empty()

        when: 'income fails to update'
        def either = service.updateIncome(new IncomeIdentifier(randomUUID()), newSampleIncomeCommand())

        then: 'not found failure result is present'
        assert either.isLeft()

        and: 'not found failure for provided income'
        assert either.getLeft() == Failure.ofNotFound('Income not found')
    }

    def "should update an income"() {
        setup: 'repository mock behavior and interaction'
        def incomeSourceId = fromString("2298dfbc-4eb3-4d83-95f4-dd7a56d21136")
        1 * incomeSources.existsByIdAndAccount(_ as IncomeSourceIdentifier, _ as AccountIdentifier) >> TRUE
        1 * incomes.findById(_ as IncomeIdentifier) >> Optional.of(newSampleIncome())
        1 * incomeSources.getReferenceById(_ as IncomeSourceIdentifier) >> newSampleIncomeSource(id: [id: incomeSourceId.toString()])
        1 * incomes.save(_ as Income) >> newSampleIncome()

        def command = newSampleIncomeCommand([
                incomeSourceId: incomeSourceId.toString(),
                amount        : 55,
                paymentType   : "Card",
                incomeDate    : "2023-10-17",
                description   : "Profit"
        ])

        when: 'income is updated'
        def uuid = fromString('3b257779-a5db-4e87-9365-72c6f8d4977d')
        def either = service.updateIncome(new IncomeIdentifier(uuid), command)

        then: 'updated income is present'
        assert either.isRight()

        and: 'income values match command request'
        assert either.get() == newSampleIncome([
                incomeSource: newSampleIncomeSource(id: [id: incomeSourceId.toString()]),
                amount      : AMOUNT + [amount: 55.00],
                paymentType : "CARD",
                incomeDate  : "2023-10-17",
                description : "Profit"
        ])
    }
}
