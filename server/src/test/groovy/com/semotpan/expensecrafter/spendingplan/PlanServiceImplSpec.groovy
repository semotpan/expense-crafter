package com.semotpan.expensecrafter.spendingplan

import com.semotpan.expensecrafter.shared.Currencies
import com.semotpan.expensecrafter.shared.Failure
import org.apache.commons.lang3.RandomStringUtils
import org.javamoney.moneta.Money
import spock.lang.Specification
import spock.lang.Tag

import static com.semotpan.expensecrafter.account.Account.AccountIdentifier
import static com.semotpan.expensecrafter.spendingplan.DataSamples.*
import static com.semotpan.expensecrafter.spendingplan.Plan.PlanIdentifier
import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE

@Tag("unit")
class PlanServiceImplSpec extends Specification {

    Plans plans
    PlanServiceImpl service

    def setup() {
        plans = Mock()
        service = new PlanServiceImpl(plans)
    }

    def "should fail plan creation when accountId is null"() {
        given: 'new command with null accountId'
        def command = newSamplePlanCreateCommand(accountId: null)

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on accountId field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('accountId')
                        .message('AccountId cannot be null')
                        .build()
        ])
    }

    def "should fail plan creation when name is invalid"() {
        given: 'new command with invalid name'
        def command = newSamplePlanCreateCommand(name: name)

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on name field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('name')
                        .message(failMessage)
                        .rejectedValue(name)
                        .build()
        ])

        where:
        name                                      | failMessage
        null                                      | "Name cannot be empty"
        '  '                                      | "Name cannot be empty"
        RandomStringUtils.random(101, true, true) | "Name overflow, max length allowed '100'"
    }

    def "should fail plan creation when amount is invalid"() {
        given: 'new command with invalid amount'
        def command = newSamplePlanCreateCommand(amount: amount)

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on amount field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
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

    def "should fail plan creation when second jar's name is empty"() {
        given: "new command with second jar's name null"
        def command = newSamplePlanCreateCommand(jars: [
                newSampleJarCreateCommand(), newSampleJarCreateCommand(name: null)
        ])

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on jars field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('jars')
                        .message("Jars '2' has validation failures: 'Name cannot be empty'")
                        .build()
        ])
    }

    def "should fail plan creation when first jar's percentage is invalid"() {
        given: "new command with first invalid jar's percentage value"
        def command = newSamplePlanCreateCommand(jars: [newSampleJarCreateCommand(percentage: 101)])

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on jars field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('jars')
                        .message("Jars '1' has validation failures: 'Percentage value must be between 0 and 100'")
                        .build()
        ])
    }

    def "should fail plan creation when jars name duplicate"() {
        given: "new command with same jars name"
        def command = newSamplePlanCreateCommand(jars: [
                newSampleJarCreateCommand(), newSampleJarCreateCommand()
        ])

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on jars field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('jars')
                        .message("Jars name 'Jar Name' already exists")
                        .build()
        ])
    }

    def "should fail plan creation when jars total percentage greater than 100"() {
        given: "new command with invalid total jars percentage "
        def command = newSamplePlanCreateCommand(jars: [
                newSampleJarCreateCommand(name: "Jar", percentage: 60), newSampleJarCreateCommand()
        ])

        when: 'plan fails to create'
        def either = service.createPlan(command)

        then: 'failure result is present'
        assert either.isLeft()

        and: 'validation failure on jars field'
        assert either.getLeft() == Failure.ofValidation('Failures on spending plan create request', [
                Failure.FieldViolation.builder()
                        .field('jars')
                        .message("Jars total percentage sum must be max 100")
                        .rejectedValue('110')
                        .build()
        ])
    }

    def "should fail plan creation when plan name already exists"() {
        setup: 'repository mock behavior and interaction'
        1 * plans.existsByNameAndAccount(_ as String, _ as AccountIdentifier) >> TRUE

        when: 'plan fails to create'
        def either = service.createPlan(newSamplePlanCreateCommand())

        then: 'not found result is present'
        assert either.isLeft()

        and: 'failure value matches'
        assert either.getLeft() == Failure.ofConflict("Spending plan name already exists")
    }

    def "should create a new spending plan"() {
        setup: 'repository mock behavior and interaction'
        1 * plans.existsByNameAndAccount(_ as String, _ as AccountIdentifier) >> FALSE
        1 * plans.save({ Plan p ->
            p == newSamplePlan([
                    id               : [id: p.getId().id()],
                    creationTimestamp: p.getCreationTimestamp().toString()
            ])

        }) >> newSamplePlan()

        when: 'new plan is created'
        def either = service.createPlan(newSamplePlanCreateCommand())

        then: 'plan identifier is available'
        assert either.isRight()
        assert either.get() instanceof PlanIdentifier
    }

    def "should create a new spending plan with jars"() {
        setup: 'repository mock behavior and interaction'
        1 * plans.existsByNameAndAccount(_ as String, _ as AccountIdentifier) >> FALSE
        1 * plans.save({ Plan p ->
            assert p.getName() == "My spending plan"
            assert p.getAmount() == Money.of(1000, Currencies.EURO)

            assert p.getJars().get(0).getName() == "Jar Name"
            assert p.getJars().get(0).getPercentage() == 50
            assert p.getJars().get(0).getAmountToReach() == Money.of(500, Currencies.EURO)
            assert p.getJars().get(0).getPlan() == p
        }) >> null

        var command = newSamplePlanCreateCommand(jars: [
                newSampleJarCreateCommand()
        ])

        when: 'new plan is created'
        def either = service.createPlan(command)

        then: 'plan identifier is available'
        assert either.isRight()
        assert either.get() instanceof PlanIdentifier
    }
}
