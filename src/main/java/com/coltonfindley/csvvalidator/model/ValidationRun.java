package com.coltonfindley.csvvalidator.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity // maps to database table
@Table(name = "validation_runs") // maps to validation_runs
public class ValidationRun {

    // columns in validation_runs table
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // table relationship
    // only this side maps relationship because there is no need for all the runs of a rule set
    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "rule_set_id", nullable = false) // foreign key
    private RuleSet ruleSet;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "failed_rows", nullable = false)
    private int failedRows;

    @Enumerated(EnumType.STRING) // store the enum's name, from RunStatus.java
    @Column(nullable = false, length = 20)
    private RunStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // set createdAt before the first insert of an entity
    @PrePersist 
    void onCreate() {
        createdAt = Instant.now();
    }

    // getters and setters
    public Long getId() {return id;}

    public RuleSet getRuleSet() {return ruleSet;}
    public void setRuleSet(RuleSet ruleSet) {this.ruleSet = ruleSet;}

    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}

    public int getTotalRows() {return totalRows;}
    public void setTotalRows(int totalRows) {this.totalRows = totalRows;}

    public int getFailedRows() {return failedRows;}
    public void setFailedRows(int failedRows) {this.failedRows = failedRows;}

    public RunStatus getStatus() {return status;}
    public void setRunStatus(RunStatus status) {this.status = status;}

    public Instant getCreatedAt() {return createdAt;}
    // createdAt is set in onCreate()
}
