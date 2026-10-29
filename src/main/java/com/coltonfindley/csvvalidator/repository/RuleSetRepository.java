package com.coltonfindley.csvvalidator.repository;

import com.coltonfindley.csvvalidator.model.RuleSet;
import org.springframework.data.jpa.repository.JpaRepository;

// Extending JpaRepository<EntityType, IdType> gives save, findById, findAll, deleteById, count, etc.
// manages rule set names
public interface RuleSetRepository extends JpaRepository<RuleSet, Long> {
    // if a rule set exists
    boolean existsByName(String name);
}
