package com.coltonfindley.csvvalidator.model;

import jakarta.persistence.*;

@Entity 
@Table(name = "row_erros") // maps to row_errors table
public class RowError {
    
    // columns in row_errors
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // many errors in one run
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false) // foreign key
    private ValidationRun run;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "column_name", nullable = false, length = 100)
    private String columnName;

    @Enumerated(EnumType.STRING) // name is from RuleType.java
    @Column(name = "rule_type", nullable = false, length = 30)
    private RuleType ruleType;

    @Column(nullable = false, length = 500)
    private String message;

    // getters and setters
    public Long getId() { return id; }

    public ValidationRun getRun() { return run; }
    public void setRun(ValidationRun run) { this.run = run; }

    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }

    public RuleType getRuleType() { return ruleType; }
    public void setRuleType(RuleType ruleType) { this.ruleType = ruleType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
