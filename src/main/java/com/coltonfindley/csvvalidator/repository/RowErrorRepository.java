package com.coltonfindley.csvvalidator.repository;

import com.coltonfindley.csvvalidator.model.RowError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// store and retrieve low-level errors for a run
public interface RowErrorRepository extends JpaRepository<RowError, Long> {
    
    Page<RowError> findByRunId(Long runId, Pageable pageable);
}
