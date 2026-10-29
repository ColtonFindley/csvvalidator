package com.coltonfindley.csvvalidator.repository;

import com.coltonfindley.csvvalidator.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest // starts Spring application
@Testcontainers // runs on Testcontainers
@Transactional   // each test runs in a transaction that is rolled back at the end
class RepositoryIntegrationTests {

    // start a Postgres Docker container
    @Container
    @ServiceConnection 
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    // create repository elements
    @Autowired RuleSetRepository ruleSetRepository;
    @Autowired RuleRepository ruleRepository;
    @Autowired ValidationRunRepository validationRunRepository;
    @Autowired RowErrorRepository rowErrorRepository;

    // JPA entity manager
    @PersistenceContext EntityManager entityManager;

    // first test - save a rule set and read it back
    @Test
    void savesRuleSetWithRules_andReadsThemBack() {
        RuleSet ruleSet = new RuleSet(); // new rule set
        ruleSet.setName("customers");
        ruleSet.setDescription("Customer export rules");

        Rule required = new Rule(); // new rule object, rule is required
        required.setColumnName("email");
        required.setRuleType(RuleType.REQUIRED);

        Rule range = new Rule(); // rule object, range of input
        range.setColumnName("age");
        range.setRuleType(RuleType.RANGE);
        range.setMinValue(new BigDecimal("0"));
        range.setMaxValue(new BigDecimal("120"));

        // add rules to rule set
        ruleSet.addRule(required);
        ruleSet.addRule(range);

        ruleSetRepository.saveAndFlush(ruleSet);   // flush = actually send the SQL now
        entityManager.clear(); // clear cache

        // find rule set in database
        RuleSet found = ruleSetRepository.findById(ruleSet.getId()).orElseThrow();

        assertThat(found.getCreatedAt()).isNotNull();   // prove that @PrePersist worked
        // check if the two defined rules exist
        assertThat(found.getRules()).hasSize(2);
        // check if defined columns names exist
        assertThat(found.getRules()).extracting(Rule::getColumnName)
            .containsExactlyInAnyOrder("email", "age");
        // if range's type was stored as a RANGE type
        Rule foundRange = found.getRules().stream()
            .filter(r -> r.getRuleType() == RuleType.RANGE).findFirst().orElseThrow();
        // check if the max value is 120
        assertThat(foundRange.getMaxValue()).isEqualByComparingTo("120");
    }

    // second test - reject duplicate rule set names
    @Test
    void rejectsDuplicateRuleSetName() {
        ruleSetRepository.saveAndFlush(newRuleSet("customers"));

        assertThatThrownBy(() -> ruleSetRepository.saveAndFlush(newRuleSet("customers")))
            .isInstanceOf(DataIntegrityViolationException.class);   // UNIQUE constraint on name
    }

    // third test - delete rule set
    @Test 
    void deletingRuleSet_removesItsRulesRunsAndErrors() {
        // create rule set
        RuleSet ruleSet = newRuleSet("customers");

        // create and add rule
        Rule rule = new Rule();
        rule.setColumnName("email");
        rule.setRuleType(RuleType.REQUIRED);
        ruleSet.addRule(rule);
        ruleSetRepository.saveAndFlush(ruleSet);

        ValidationRun run = saveRun(ruleSet);
        saveError(run, 1);
        entityManager.clear();

        // test that all three kinds of rows exist before delete
        assertThat(ruleRepository.count()).isEqualTo(1);
        assertThat(validationRunRepository.count()).isEqualTo(1);
        assertThat(rowErrorRepository.count()).isEqualTo(1);

        // delete rule set, flush the delete statement now, clear cache
        ruleSetRepository.deleteById(ruleSet.getId());
        ruleSetRepository.flush();
        entityManager.clear();

        // test rows are gone
        assertThat(ruleRepository.count()).isZero();
        assertThat(validationRunRepository.count()).isZero();
        assertThat(rowErrorRepository.count()).isZero();
    }

    // fourth test - find errors
    @Test
    void findsErrorsByRunId_pagedAndSorted() {
        // create new rule set and 2 validation runs
        RuleSet ruleSet = ruleSetRepository.saveAndFlush(newRuleSet("customers"));
        ValidationRun run = saveRun(ruleSet);
        ValidationRun otherRun = saveRun(ruleSet);

        // test sorting by inserting out of order
        for (int row = 5; row >= 1; row--) {
            saveError(run, row);
        }
        // belongs to a different run; must not appear
        saveError(otherRun, 99); 

        // holds the items for this page and metadata about the full result set
        Page<RowError> page = rowErrorRepository.findByRunId(
            run.getId(), PageRequest.of(0, 2, Sort.by("rowNumber")));

        assertThat(page.getTotalElements()).isEqualTo(5); // only this run's errors
        assertThat(page.getTotalPages()).isEqualTo(3); // 5 errors, 2 per page
        assertThat(page.getContent()).extracting(RowError::getRowNumber)
            .containsExactly(1, 2); // sorted, first page
    }

    // helper functions for readablility
    // make a new rule set
    private RuleSet newRuleSet(String name) {
        RuleSet ruleSet = new RuleSet();
        ruleSet.setName(name);
        return ruleSet;
    }

    // save a validatoin run
    private ValidationRun saveRun(RuleSet ruleSet) {
        ValidationRun run = new ValidationRun();
        run.setRuleSet(ruleSet);
        run.setFilename("data.csv");
        run.setTotalRows(10);
        run.setFailedRows(5);
        run.setStatus(RunStatus.COMPLETED);
        return validationRunRepository.saveAndFlush(run);
    }

    // save an error
    private void saveError(ValidationRun run, int rowNumber) {
        RowError error = new RowError();
        error.setRun(run);
        error.setRowNumber(rowNumber);
        error.setColumnName("email");
        error.setRuleType(RuleType.REQUIRED);
        error.setMessage("email is required");
        rowErrorRepository.saveAndFlush(error);
    }
}
