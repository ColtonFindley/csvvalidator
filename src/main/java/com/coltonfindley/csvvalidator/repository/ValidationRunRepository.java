package com.coltonfindley.csvvalidator.repository;

import com.coltonfindley.csvvalidator.model.ValidationRun;
import org.springframework.data.jpa.repository.JpaRepository;

// store and retrieve validation runs
public interface ValidationRunRepository extends JpaRepository<ValidationRun, Long> {
    
}
