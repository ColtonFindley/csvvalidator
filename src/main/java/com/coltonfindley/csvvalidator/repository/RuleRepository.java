package com.coltonfindley.csvvalidator.repository;

import com.coltonfindley.csvvalidator.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    // find a rule set by id 
    List<Rule> findByRuleSetId(Long ruleSetId);
}
